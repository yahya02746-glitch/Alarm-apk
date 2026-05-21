package com.example.ui

import android.os.SystemClock
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppLanguage
import kotlinx.coroutines.delay

@Composable
fun StopwatchTimerView(
    appLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    var activeTimerSection by remember { mutableStateOf("stopwatch") } // stopwatch or timer

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Switch Selector pill
        Row(
            modifier = Modifier
                .width(260.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val stopwatchSelected = activeTimerSection == "stopwatch"
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (stopwatchSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeTimerSection = "stopwatch" }
            ) {
                Text(
                    text = L10n.get("stopwatch_title", appLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (stopwatchSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!stopwatchSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeTimerSection = "timer" }
            ) {
                Text(
                    text = L10n.get("timer_title", appLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!stopwatchSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(
            targetState = activeTimerSection,
            transitionSpec = {
                (fadeIn() + slideInHorizontally()).togetherWith(fadeOut() + slideOutHorizontally())
            },
            label = "timer_section_anim"
        ) { section ->
            if (section == "stopwatch") {
                StopwatchComponent(appLanguage = appLanguage)
            } else {
                TimerComponent(appLanguage = appLanguage)
            }
        }
    }
}

@Composable
fun StopwatchComponent(appLanguage: AppLanguage) {
    var isRunning by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableLongStateOf(0L) } // in ms

    LaunchedEffect(isRunning) {
        if (isRunning) {
            var lastTime = SystemClock.elapsedRealtime()
            while (isRunning) {
                delay(10)
                val now = SystemClock.elapsedRealtime()
                timeElapsed += (now - lastTime)
                lastTime = now
            }
        }
    }

    val minutes = (timeElapsed / 60000) % 60
    val seconds = (timeElapsed / 1000) % 60
    val centiseconds = (timeElapsed / 10) % 100
    val formattedTime = String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = L10n.get("stopwatch_title", appLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = formattedTime,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            IconButton(
                onClick = {
                    isRunning = false
                    timeElapsed = 0L
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = L10n.get("reset", appLanguage),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Start/Pause Floating Action Button Style
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Text(
                        text = if (isRunning) L10n.get("pause", appLanguage) else L10n.get("start", appLanguage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimerComponent(appLanguage: AppLanguage) {
    var pickHours by remember { mutableIntStateOf(0) }
    var pickMinutes by remember { mutableIntStateOf(10) } // default 10 minutes
    var pickSeconds by remember { mutableIntStateOf(0) }

    var totalDuration by remember { mutableLongStateOf(0L) } // in seconds
    var remainingSeconds by remember { mutableLongStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (isTimerRunning && remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }
            if (remainingSeconds == 0L) {
                isTimerRunning = false
            }
        }
    }

    val displayHours = remainingSeconds / 3600
    val displayMinutes = (remainingSeconds % 3600) / 60
    val displaySeconds = remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d:%02d", displayHours, displayMinutes, displaySeconds)

    val progress = remember(remainingSeconds, totalDuration) {
        if (totalDuration > 0) {
            remainingSeconds.toFloat() / totalDuration.toFloat()
        } else {
            0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isTimerRunning && remainingSeconds == 0L) {
            // Configuration mode
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = L10n.get("timer_title", appLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TimerValuePicker(
                            value = pickHours,
                            onValueChange = { pickHours = it.coerceIn(0, 23) },
                            title = if (appLanguage == AppLanguage.ARABIC) "س" else "H"
                        )
                        TimerValuePicker(
                            value = pickMinutes,
                            onValueChange = { pickMinutes = it.coerceIn(0, 59) },
                            title = if (appLanguage == AppLanguage.ARABIC) "د" else "M"
                        )
                        TimerValuePicker(
                            value = pickSeconds,
                            onValueChange = { pickSeconds = it.coerceIn(0, 59) },
                            title = if (appLanguage == AppLanguage.ARABIC) "ث" else "S"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Preset Quick Adders row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(1, 5, 10, 15).forEach { mins ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(19.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(19.dp))
                                    .clickable {
                                        pickHours = 0
                                        pickMinutes = mins
                                        pickSeconds = 0
                                    }
                            ) {
                                Text(
                                    text = "+$mins " + (if (appLanguage == AppLanguage.ARABIC) "دقائق" else "min"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Running/Paused state with beautiful sweep Loader
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
                    .padding(16.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    strokeWidth = 10.dp,
                    modifier = Modifier.fillMaxSize()
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset/Clear button
            IconButton(
                onClick = {
                    isTimerRunning = false
                    remainingSeconds = 0L
                    totalDuration = 0L
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = L10n.get("reset", appLanguage),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Start/Pause Button
            Button(
                onClick = {
                    if (isTimerRunning) {
                        isTimerRunning = false
                    } else {
                        if (remainingSeconds == 0L) {
                            val secondsCalc = (pickHours * 3600 + pickMinutes * 60 + pickSeconds).toLong()
                            if (secondsCalc > 0) {
                                totalDuration = secondsCalc
                                remainingSeconds = secondsCalc
                                isTimerRunning = true
                            }
                        } else {
                            isTimerRunning = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Text(
                        text = if (isTimerRunning) L10n.get("pause", appLanguage) else L10n.get("start", appLanguage),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TimerValuePicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        IconButton(
            onClick = { onValueChange(value + 1) },
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = String.format("%02d", value),
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        IconButton(
            onClick = { onValueChange(value - 1) },
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Sub", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
        }
    }
}
