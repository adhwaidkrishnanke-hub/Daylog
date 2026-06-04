package com.eadiat.daylog.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import com.eadiat.daylog.MainActivity
import com.eadiat.daylog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.eadiat.daylog.data.DaylogDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DaylogWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.widget_layout)
    val today = LocalDate.now()
    val dateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val displayDate = today.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

    // Tap to open app
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

    CoroutineScope(Dispatchers.IO).launch {
        val db = DaylogDatabase.getDatabase(context)
        val tasks = db.dayTaskDao().getTasksForDate(dateStr)
        val done = tasks.count { it.isDone }
        val total = tasks.size

        val taskText = when {
            total == 0 -> "No tasks yet — tap to add"
            done == total -> "All $total tasks done 🎉"
            else -> "$done / $total tasks done"
        }

        val progressText = when {
            total == 0 -> ""
            done == total -> "Perfect day! 🔥"
            else -> "${(done * 100 / total)}% complete"
        }

        views.setTextViewText(R.id.widget_date, displayDate)
        views.setTextViewText(R.id.widget_tasks, taskText)
        views.setTextViewText(R.id.widget_progress, progressText)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}