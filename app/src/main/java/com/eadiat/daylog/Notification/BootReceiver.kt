package com.eadiat.daylog.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("daylog_prefs", Context.MODE_PRIVATE)
            val hour = prefs.getInt("reminder_hour", 21)
            val minute = prefs.getInt("reminder_minute", 0)
            val enabled = prefs.getBoolean("reminder_enabled", false)
            if (enabled) {
                NotificationScheduler.scheduleDaily(context, hour, minute)
            }
        }
    }
}