package com.example.ui

import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.database.Alarm
import com.example.data.database.Task
import com.example.viewmodel.AlarmTaskViewModel
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.ThemeColor
import com.example.viewmodel.ThemeMode
import com.example.viewmodel.AlarmSoundSource
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: AlarmTaskViewModel) {
    val alarms by viewModel.alarms.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    // Persistent Settings
    val appLanguage by viewModel.appLanguage.collectAsState()
    val themeColor by viewModel.themeColor.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val soundSource by viewModel.soundSource.collectAsState()
    val customSoundUri by viewModel.customSoundUri.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf("alarms") } // alarms, tasks, stopwatch, settings

    var showAddAlarmDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Dialog state holders
    var alarmHour by remember { mutableIntStateOf(7) }
    var alarmMinute by remember { mutableIntStateOf(0) }
    var alarmLabel by remember { mutableStateOf("") }
    var alarmRepeatWeeks by remember { mutableStateOf(setOf<Int>()) } // 1=Sun, 2=Mon...

    var taskTitle by remember { mutableStateOf("") }
    var taskDesc by remember { mutableStateOf("") }
    var taskHour by remember { mutableStateOf<Int?>(null) }
    var taskMinute by remember { mutableStateOf<Int?>(null) }

    // Calendar weekdays mappings
    val daysOfWeekSelectedList = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )

    // Calculate the next active alarm relative to current time
    val nextEnabledAlarm = remember(alarms) {
        alarms.filter { it.isEnabled }
            .minByOrNull { alarm ->
                val calendar = Calendar.getInstance()
                val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
                val nowMinute = calendar.get(Calendar.MINUTE)
                val alarmTimeInMins = alarm.hour * 60 + alarm.minute
                val nowTimeInMins = nowHour * 60 + nowMinute
                if (alarmTimeInMins > nowTimeInMins) {
                    alarmTimeInMins - nowTimeInMins
                } else {
                    (24 * 60) - nowTimeInMins + alarmTimeInMins
                }
            }
    }

    // Force orientation RTL for Arabic, LTR for English/French
    val layoutDirection = if (appLanguage == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // HEADER AREA with custom translucent YM Logo aligned
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_ym_logo),
                                contentDescription = "YM Logo Landscape",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .size(64.dp)
                                    .testTag("drawer_ym_logo")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "YM",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-1).sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = L10n.get("app_name", appLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Drawer Navigation Items (Localized)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = { Text(L10n.get("alarms_tab", appLanguage), fontWeight = FontWeight.Bold) },
                        selected = currentTab == "alarms",
                        onClick = {
                            currentTab = "alarms"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = { Text(L10n.get("tasks_tab", appLanguage), fontWeight = FontWeight.Bold) },
                        selected = currentTab == "tasks",
                        onClick = {
                            currentTab = "tasks"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = { Text(L10n.get("stopwatch_tab", appLanguage), fontWeight = FontWeight.Bold) },
                        selected = currentTab == "stopwatch",
                        onClick = {
                            currentTab = "stopwatch"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        label = { Text(L10n.get("settings_tab", appLanguage), fontWeight = FontWeight.Bold) },
                        selected = currentTab == "settings",
                        onClick = {
                            currentTab = "settings"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = when (currentTab) {
                                    "alarms" -> L10n.get("alarms_title", appLanguage)
                                    "tasks" -> L10n.get("tasks_title", appLanguage)
                                    "stopwatch" -> L10n.get("stopwatch_tab", appLanguage)
                                    else -> L10n.get("settings_tab", appLanguage)
                                },
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        navigationIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ym_logo),
                                    contentDescription = "YM Logo Inside Topbar",
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clickable { scope.launch { drawerState.open() } }
                                        .testTag("topbar_ym_logo")
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                bottomBar = {
                    // Modern capsule dynamic bottom navigation bar supporting 4 items
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars),
                        tonalElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Tab 1: Alarms
                                NavigationTabItem(
                                    selected = currentTab == "alarms",
                                    icon = Icons.Default.Notifications,
                                    label = L10n.get("alarms_tab", appLanguage),
                                    onClick = { currentTab = "alarms" }
                                )

                                // Tab 2: Tasks
                                NavigationTabItem(
                                    selected = currentTab == "tasks",
                                    icon = Icons.Default.CheckCircle,
                                    label = L10n.get("tasks_tab", appLanguage),
                                    onClick = { currentTab = "tasks" }
                                )

                                // Tab 3: Stopwatch
                                NavigationTabItem(
                                    selected = currentTab == "stopwatch",
                                    icon = Icons.Default.PlayArrow,
                                    label = L10n.get("stopwatch_tab", appLanguage),
                                    onClick = { currentTab = "stopwatch" }
                                )

                                // Tab 4: Settings
                                NavigationTabItem(
                                    selected = currentTab == "settings",
                                    icon = Icons.Default.Settings,
                                    label = L10n.get("settings_tab", appLanguage),
                                    onClick = { currentTab = "settings" }
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (currentTab == "alarms" || currentTab == "tasks") {
                        FloatingActionButton(
                            onClick = {
                                if (currentTab == "alarms") {
                                    showAddAlarmDialog = true
                                } else {
                                    showAddTaskDialog = true
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .testTag("add_fab")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = if (currentTab == "alarms") "Add Alarm" else "Add Task",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            fadeIn().togetherWith(fadeOut())
                        },
                        label = "main_tabs_animation"
                    ) { tab ->
                        when (tab) {
                            "alarms" -> {
                                if (alarms.isEmpty()) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        UpcomingAlarmHero(nextAlarm = null, appLanguage = appLanguage)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        EmptyStateView(
                                            icon = Icons.Default.Notifications,
                                            title = L10n.get("no_alarms", appLanguage),
                                            description = L10n.get("no_alarms_desc", appLanguage)
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        item {
                                            UpcomingAlarmHero(nextAlarm = nextEnabledAlarm, appLanguage = appLanguage)
                                        }
                                        
                                        item {
                                            Text(
                                                text = L10n.get("active_alarms", appLanguage),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        items(alarms) { alarm ->
                                            AlarmCard(
                                                alarm = alarm,
                                                appLanguage = appLanguage,
                                                onToggle = { viewModel.toggleAlarm(alarm) },
                                                onDelete = { viewModel.deleteAlarm(alarm) }
                                            )
                                        }
                                    }
                                }
                            }
                            "tasks" -> {
                                if (tasks.isEmpty()) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        TasksCompletionHero(tasksList = emptyList(), appLanguage = appLanguage)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        EmptyStateView(
                                            icon = Icons.Default.CheckCircle,
                                            title = L10n.get("no_tasks", appLanguage),
                                            description = L10n.get("no_tasks_desc", appLanguage)
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        item {
                                            TasksCompletionHero(tasksList = tasks, appLanguage = appLanguage)
                                        }

                                        item {
                                            Text(
                                                text = L10n.get("daily_tasks_list", appLanguage),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }

                                        items(tasks) { task ->
                                            TaskCard(
                                                task = task,
                                                appLanguage = appLanguage,
                                                onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                                                onDelete = { viewModel.deleteTask(task) }
                                            )
                                        }
                                    }
                                }
                            }
                            "stopwatch" -> {
                                StopwatchTimerView(appLanguage = appLanguage)
                            }
                            else -> {
                                SettingsView(
                                    appLanguage = appLanguage,
                                    themeColor = themeColor,
                                    themeMode = themeMode,
                                    soundSource = soundSource,
                                    customSoundUri = customSoundUri,
                                    onLanguageChanged = { viewModel.setLanguage(it) },
                                    onThemeColorChanged = { viewModel.setThemeColor(it) },
                                    onThemeModeChanged = { viewModel.setThemeMode(it) },
                                    onSoundSourceChanged = { viewModel.setSoundSource(it) },
                                    onCustomSoundUriSelected = { viewModel.setCustomSoundUri(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOGS SECTION ---

        // 1. ADD ALARM DIALOG WITH SPECIFIC WEEKDAY STRINGS "أ إ ث أ خ ج س"
        if (showAddAlarmDialog) {
            val context = LocalContext.current
            
            Dialog(onDismissRequest = { showAddAlarmDialog = false }) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = L10n.get("add_alarm_title", appLanguage),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .clickable {
                                    TimePickerDialog(
                                        context,
                                        { _, hour, min ->
                                            alarmHour = hour
                                            alarmMinute = min
                                        },
                                        alarmHour,
                                        alarmMinute,
                                        false
                                    ).show()
                                }
                                .padding(horizontal = 28.dp, vertical = 18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val amPmText = if (alarmHour >= 12) L10n.get("pm", appLanguage) else L10n.get("am", appLanguage)
                            val displayHour = when {
                                alarmHour == 0 -> 12
                                alarmHour > 12 -> alarmHour - 12
                                else -> alarmHour
                            }
                            Text(
                                text = String.format("%02d:%02d %s", displayHour, alarmMinute, amPmText),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = alarmLabel,
                            onValueChange = { alarmLabel = it },
                            label = { Text(L10n.get("alarm_label_hint", appLanguage)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = L10n.get("alarm_repeat_days_title", appLanguage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = if (appLanguage == AppLanguage.ARABIC) TextAlign.Right else TextAlign.Left
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val weekdayLettersAr = listOf("أ", "إ", "ث", "أ", "خ", "ج", "س")
                            val weekdayLettersFr = listOf("D", "L", "M", "M", "J", "V", "S")
                            val weekdayLettersEn = listOf("S", "M", "T", "W", "T", "F", "S")
                            val letters = when (appLanguage) {
                                AppLanguage.ENGLISH -> weekdayLettersEn
                                AppLanguage.FRENCH -> weekdayLettersFr
                                else -> weekdayLettersAr
                            }

                            daysOfWeekSelectedList.forEachIndexed { idx, calValue ->
                                val letter = letters.getOrNull(idx) ?: "أ"
                                val isSelected = alarmRepeatWeeks.contains(calValue)
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                        .clickable {
                                            alarmRepeatWeeks = if (isSelected) {
                                                alarmRepeatWeeks - calValue
                                            } else {
                                                alarmRepeatWeeks + calValue
                                            }
                                        }
                                ) {
                                    Text(
                                        text = letter,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val repeatDaysStr = alarmRepeatWeeks.joinToString(",")
                                    viewModel.addAlarm(
                                        hour = alarmHour,
                                        minute = alarmMinute,
                                        label = alarmLabel,
                                        repeatDays = repeatDaysStr
                                    )
                                    alarmLabel = ""
                                    alarmRepeatWeeks = emptySet()
                                    showAddAlarmDialog = false
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(L10n.get("save", appLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showAddAlarmDialog = false },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(L10n.get("cancel", appLanguage), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. ADD TASK DIALOG
        if (showAddTaskDialog) {
            val context = LocalContext.current
            
            Dialog(onDismissRequest = { showAddTaskDialog = false }) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = L10n.get("add_task_title", appLanguage),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text(L10n.get("task_title_hint", appLanguage)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = taskDesc,
                            onValueChange = { taskDesc = it },
                            label = { Text(L10n.get("task_desc_hint", appLanguage)) },
                            maxLines = 3,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = L10n.get("task_time_title", appLanguage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = if (appLanguage == AppLanguage.ARABIC) TextAlign.Right else TextAlign.Left
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                .clickable {
                                    val hourNow = taskHour ?: 12
                                    val minNow = taskMinute ?: 0
                                    TimePickerDialog(
                                        context,
                                        { _, h, m ->
                                            taskHour = h
                                            taskMinute = m
                                        },
                                        hourNow,
                                        minNow,
                                        false
                                    ).show()
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (taskHour != null && taskMinute != null) {
                                        val amPm = if (taskHour!! >= 12) L10n.get("pm", appLanguage) else L10n.get("am", appLanguage)
                                        val displayH = when {
                                            taskHour!! == 0 -> 12
                                            taskHour!! > 12 -> taskHour!! - 12
                                            else -> taskHour!!
                                        }
                                        String.format("%02d:%02d %s", displayH, taskMinute, amPm)
                                    } else {
                                        L10n.get("task_select_time_btn", appLanguage)
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (taskHour != null) {
                                IconButton(
                                    onClick = {
                                        taskHour = null
                                        taskMinute = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (taskTitle.isNotEmpty()) {
                                        viewModel.addTask(
                                            title = taskTitle,
                                            description = taskDesc,
                                            hour = taskHour,
                                            minute = taskMinute
                                        )
                                        taskTitle = ""
                                        taskDesc = ""
                                        taskHour = null
                                        taskMinute = null
                                        showAddTaskDialog = false
                                    }
                                },
                                enabled = taskTitle.isNotEmpty(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(L10n.get("add", appLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showAddTaskDialog = false },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(L10n.get("cancel", appLanguage), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationTabItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(60.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.secondaryContainer
                    else Color.Transparent
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1
        )
    }
}

@Composable
fun UpcomingAlarmHero(nextAlarm: Alarm?, appLanguage: AppLanguage) {
    val remainingText = remember(nextAlarm, appLanguage) {
        if (nextAlarm == null) {
            L10n.get("no_time_remaining", appLanguage)
        } else {
            val calendar = Calendar.getInstance()
            val nowHour = calendar.get(Calendar.HOUR_OF_DAY)
            val nowMinute = calendar.get(Calendar.MINUTE)

            var diffMinutes = (nextAlarm.hour * 60 + nextAlarm.minute) - (nowHour * 60 + nowMinute)
            if (diffMinutes <= 0) {
                diffMinutes += 24 * 60
            }
            val hours = diffMinutes / 60
            val minutes = diffMinutes % 60

            val labelPrefix = L10n.get("time_remaining_label", appLanguage)
            val durationText = when (appLanguage) {
                AppLanguage.ENGLISH -> "In $hours Hr, $minutes Min"
                AppLanguage.FRENCH -> "Dans $hours H, $minutes Min"
                else -> "خلال $hours ساعة و $minutes دقيقة"
            }
            "$labelPrefix $durationText"
        }
    }
    
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("upcoming_alarm_hero")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = L10n.get("upcoming_alarm", appLanguage),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (nextAlarm != null) {
                val amPmStr = if (nextAlarm.hour >= 12) L10n.get("pm", appLanguage) else L10n.get("am", appLanguage)
                val displayH = when {
                    nextAlarm.hour == 0 -> 12
                    nextAlarm.hour > 12 -> nextAlarm.hour - 12
                    else -> nextAlarm.hour
                }
                val timeStr = String.format("%02d:%02d", displayH, nextAlarm.minute)

                Text(
                    text = "$timeStr $amPmStr",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Text(
                    text = nextAlarm.label.ifEmpty { L10n.get("alarm_tag_default", appLanguage) },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = remainingText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "--:--",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Text(
                    text = L10n.get("no_active_alarms", appLanguage),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun TasksCompletionHero(tasksList: List<Task>, appLanguage: AppLanguage) {
    val total = tasksList.size
    val completed = tasksList.count { it.isCompleted }
    val percentage = if (total > 0) (completed.toFloat() / total.toFloat()) else 0f
    
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tasks_completion_hero")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = L10n.get("level_performance", appLanguage),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            Text(
                text = String.format("%.0f%%", percentage * 100f),
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            
            val statusMsg = if (total > 0) {
                String.format(L10n.get("tasks_perf_status", appLanguage), completed, total)
            } else {
                L10n.get("no_tasks_added", appLanguage)
            }
            Text(
                text = statusMsg,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LinearProgressIndicator(
                progress = { percentage },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun AlarmCard(
    alarm: Alarm,
    appLanguage: AppLanguage,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (alarm.isEnabled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = alarm.label.ifEmpty { L10n.get("alarm_tag_default", appLanguage) },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (alarm.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    val amPm = if (alarm.hour >= 12) L10n.get("pm", appLanguage) else L10n.get("am", appLanguage)
                    val displayHour = when {
                        alarm.hour == 0 -> 12
                        alarm.hour > 12 -> alarm.hour - 12
                        else -> alarm.hour
                    }
                    val displayTime = String.format("%02d:%02d %s", displayHour, alarm.minute, amPm)

                    Text(
                        text = displayTime,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.5).sp,
                        color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    if (alarm.repeatDays.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = L10n.get("days_header", appLanguage) + ": " + L10n.translateRepeatDays(alarm.repeatDays, appLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    appLanguage: AppLanguage,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (task.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.primary
                        )
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion() },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Column {
                    Text(
                        text = task.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
                    )
                    if (task.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = task.description,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    task.getFormattedTime()?.let { timeStr ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            // Re-format time based on selected app language
                            val formattedTimeStr = remember(task.hour, task.minute, appLanguage) {
                                val hr = task.hour ?: 12
                                val min = task.minute ?: 0
                                val amPm = if (hr >= 12) L10n.get("pm", appLanguage) else L10n.get("am", appLanguage)
                                val displayH = when {
                                    hr == 0 -> 12
                                    hr > 12 -> hr - 12
                                    else -> hr
                                }
                                String.format("%02d:%02d %s", displayH, min, amPm)
                            }

                            Text(
                                text = String.format(L10n.get("scheduled_at", appLanguage), formattedTimeStr),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}
