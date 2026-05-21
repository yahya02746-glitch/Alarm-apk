package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.database.Repository
import com.example.ui.MainAppContent
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AlarmTaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Database and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = Repository(database.alarmDao(), database.taskDao())

        // Initialize ViewModel via Factory
        val factory = AlarmTaskViewModel.Factory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[AlarmTaskViewModel::class.java]

        // Request notification posts permission on Android 13+
        requestNotificationPermission()

        setContent {
            val themeColor by viewModel.themeColor.collectAsState(com.example.viewmodel.ThemeColor.FOREST)
            val themeMode by viewModel.themeMode.collectAsState(com.example.viewmodel.ThemeMode.LIGHT)

            MyApplicationTheme(themeColor = themeColor, themeMode = themeMode) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
