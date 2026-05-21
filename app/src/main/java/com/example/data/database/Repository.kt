package com.example.data.database

import kotlinx.coroutines.flow.Flow

class Repository(private val alarmDao: AlarmDao, private val taskDao: TaskDao) {
    val allAlarms: Flow<List<Alarm>> = alarmDao.getAllAlarms()
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getAlarmById(id: Int): Alarm? = alarmDao.getAlarmById(id)

    suspend fun insertAlarm(alarm: Alarm): Long = alarmDao.insertAlarm(alarm)

    suspend fun updateAlarm(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    suspend fun getTasksForAlarm(alarmId: Int): List<Task> = taskDao.getTasksForAlarm(alarmId)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}
