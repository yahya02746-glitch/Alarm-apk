package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import com.example.data.database.Alarm
import com.example.data.database.AppDatabase
import com.example.data.database.Repository
import com.example.data.database.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage {
    ARABIC, ENGLISH, FRENCH
}

enum class ThemeColor {
    FOREST, OCEAN, LAVENDER, GOLD
}

enum class ThemeMode {
    LIGHT, DARK
}

enum class AlarmSoundSource {
    RINGTONE, DEVICE_FILE
}

class AlarmTaskViewModel(
    application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler(application)
    private val prefs = application.getSharedPreferences("YM_SETTINGS", Context.MODE_PRIVATE)

    private val _appLanguage = MutableStateFlow(
        try {
            AppLanguage.valueOf(prefs.getString("app_language", AppLanguage.ARABIC.name) ?: AppLanguage.ARABIC.name)
        } catch (e: Exception) {
            AppLanguage.ARABIC
        }
    )
    val appLanguage = _appLanguage.asStateFlow()

    private val _themeColor = MutableStateFlow(
        try {
            ThemeColor.valueOf(prefs.getString("theme_color", ThemeColor.FOREST.name) ?: ThemeColor.FOREST.name)
        } catch (e: Exception) {
            ThemeColor.FOREST
        }
    )
    val themeColor = _themeColor.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.LIGHT.name) ?: ThemeMode.LIGHT.name)
        } catch (e: Exception) {
            ThemeMode.LIGHT
        }
    )
    val themeMode = _themeMode.asStateFlow()

    private val _soundSource = MutableStateFlow(
        try {
            AlarmSoundSource.valueOf(prefs.getString("sound_source", AlarmSoundSource.RINGTONE.name) ?: AlarmSoundSource.RINGTONE.name)
        } catch (e: Exception) {
            AlarmSoundSource.RINGTONE
        }
    )
    val soundSource = _soundSource.asStateFlow()

    private val _customSoundUri = MutableStateFlow(
        prefs.getString("custom_sound_uri", null)
    )
    val customSoundUri = _customSoundUri.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
        prefs.edit().putString("app_language", language.name).apply()
    }

    fun setThemeColor(color: ThemeColor) {
        _themeColor.value = color
        prefs.edit().putString("theme_color", color.name).apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun setSoundSource(source: AlarmSoundSource) {
        _soundSource.value = source
        prefs.edit().putString("sound_source", source.name).apply()
    }

    fun setCustomSoundUri(uri: String?) {
        _customSoundUri.value = uri
        prefs.edit().putString("custom_sound_uri", uri).apply()
    }

    // Exposed Flows
    val alarms: StateFlow<List<Alarm>> = repository.allAlarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Alarm Operations
    fun addAlarm(hour: Int, minute: Int, label: String, repeatDays: String) {
        viewModelScope.launch {
            val alarm = Alarm(
                hour = hour,
                minute = minute,
                label = label.ifEmpty { "منبه" },
                isEnabled = true,
                repeatDays = repeatDays
            )
            val newId = repository.insertAlarm(alarm).toInt()
            val scheduledAlarm = alarm.copy(id = newId)
            alarmScheduler.schedule(scheduledAlarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.updateAlarm(alarm)
            if (alarm.isEnabled) {
                alarmScheduler.schedule(alarm)
            } else {
                alarmScheduler.cancel(alarm)
            }
        }
    }

    fun toggleAlarm(alarm: Alarm) {
        val updated = alarm.copy(isEnabled = !alarm.isEnabled)
        updateAlarm(updated)
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            alarmScheduler.cancel(alarm)
            repository.deleteAlarm(alarm)
        }
    }

    // Task Operations
    fun addTask(title: String, description: String, hour: Int? = null, minute: Int? = null, associatedAlarmId: Int? = null) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                isCompleted = false,
                hour = hour,
                minute = minute,
                associatedAlarmId = associatedAlarmId
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val updated = task.copy(isCompleted = !task.isCompleted)
        updateTask(updated)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Factory Class for direct instantiation in MainActivity
    class Factory(
        private val application: Application,
        private val repository: Repository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlarmTaskViewModel::class.java)) {
                return AlarmTaskViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
