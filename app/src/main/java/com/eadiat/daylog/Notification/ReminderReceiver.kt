package com.eadiat.daylog.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.eadiat.daylog.MainActivity
import com.eadiat.daylog.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "daylog_reminder",
            "Daily Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val openIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "daylog_reminder")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Daylog 📓")
            .setContentText("Time to log your day! How did it go?")
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}