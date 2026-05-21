package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean = true,
    val repeatDays: String = "" // String format like "1,2,3" (1=Mon, 2=Tue...) or empty for once
) {
    fun getFormattedTime(): String {
        val amPm = if (hour >= 12) "م" else "ص"
        val hourFormatted = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minFormatted = String.format("%02d", minute)
        return "$hourFormatted:$minFormatted $amPm"
    }

    fun getRepeatDaysList(): List<Int> {
        if (repeatDays.isEmpty()) return emptyList()
        return repeatDays.split(",").mapNotNull { it.toIntOrNull() }
    }
}
