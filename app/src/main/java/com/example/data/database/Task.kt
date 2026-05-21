package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val hour: Int? = null,
    val minute: Int? = null,
    val associatedAlarmId: Int? = null,
    val dateTimestamp: Long = System.currentTimeMillis() // Useful for history or sorting
) {
    fun getFormattedTime(): String? {
        if (hour == null || minute == null) return null
        val amPm = if (hour >= 12) "م" else "ص"
        val hourFormatted = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minFormatted = String.format("%02d", minute)
        return "$hourFormatted:$minFormatted $amPm"
    }
}
