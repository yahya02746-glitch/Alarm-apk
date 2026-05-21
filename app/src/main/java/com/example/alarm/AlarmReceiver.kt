package com.example.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getIntExtra("ALARM_ID", -1)

        if (action == ACTION_STOP_ALARM) {
            Log.d("AlarmReceiver", "Stopping alarm ringtone for ID: $alarmId")
            stopAlarmSound()
            
            // Dismiss the notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(alarmId)
            return
        }

        val hour = intent.getIntExtra("ALARM_HOUR", 0)
        val minute = intent.getIntExtra("ALARM_MINUTE", 0)
        val label = intent.getStringExtra("ALARM_LABEL") ?: "المنبه"

        Log.d("AlarmReceiver", "Alarm triggered! ID: $alarmId at $hour:$minute")

        // 1. Play Alarm Sound and Vibrate
        playAlarmSound(context)

        // 1.1 Reschedule if repeat is needed, or disable since it's one-time
        if (alarmId != -1) {
            val database = AppDatabase.getDatabase(context)
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = database.alarmDao().getAlarmById(alarmId)
                if (alarm != null) {
                    if (alarm.repeatDays.isEmpty()) {
                        // One-time alarm, disable it now that it triggered
                        val updatedAlarm = alarm.copy(isEnabled = false)
                        database.alarmDao().updateAlarm(updatedAlarm)
                    } else {
                        // Repeats: reschedule for the next occurrence
                        AlarmScheduler(context).schedule(alarm)
                    }
                }
            }
        }

        // 2. Trigger notification with actionable STOP button
        showNotification(context, alarmId, label, String.format("%02d:%02d", hour, minute))
    }

    private fun showNotification(context: Context, alarmId: Int, title: String, timeText: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ym_alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "YM Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "قناة لإنذارات تطبيق منبه YM"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Start App on Click
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop Action Intent
        val stopIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_STOP_ALARM
            putExtra("ALARM_ID", alarmId)
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Using system alarm icon for status bar
            .setContentTitle("رنين المنبه: $title")
            .setContentText("حان وقت المنبه الخاص بك مبرمج على الساعة $timeText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "إيقاف الصوت",
                stopPendingIntent
            )
            .build()

        notificationManager.notify(alarmId, notification)
    }

    private fun playAlarmSound(context: Context) {
        try {
            // Stop existing if playing
            stopAlarmSound()

            // Vibrate
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(android.os.VibrationEffect.createWaveform(longArrayOf(0, 500, 500, 500), 0))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 500, 500, 500), 0)
            }

            // Play alarm sound
            val prefs = context.getSharedPreferences("YM_SETTINGS", Context.MODE_PRIVATE)
            val soundSource = prefs.getString("sound_source", "RINGTONE")
            val customUriString = prefs.getString("custom_sound_uri", null)

            var alarmUri: Uri? = null
            if (soundSource == "DEVICE_FILE" && !customUriString.isNullOrEmpty()) {
                try {
                    alarmUri = Uri.parse(customUriString)
                } catch (e: Exception) {
                    Log.e("AlarmReceiver", "Error parsing custom sound URI", e)
                }
            }

            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            }
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            if (alarmUri != null) {
                currentRingtone = RingtoneManager.getRingtone(context, alarmUri).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        isLooping = true
                    }
                    play()
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error playing alarm sound", e)
        }
    }

    companion object {
        const val ACTION_STOP_ALARM = "com.example.alarm.ACTION_STOP_ALARM"
        private var currentRingtone: Ringtone? = null

        fun stopAlarmSound() {
            try {
                currentRingtone?.let {
                    if (it.isPlaying) {
                        it.stop()
                    }
                }
                currentRingtone = null
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error stopping alarm icon/sound", e)
            }
        }
    }
}
