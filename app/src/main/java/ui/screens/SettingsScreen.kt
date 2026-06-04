package com.eadiat.daylog.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.notification.NotificationScheduler

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("daylog_prefs", Context.MODE_PRIVATE)

    var reminderEnabled by remember {
        mutableStateOf(prefs.getBoolean("reminder_enabled", false))
    }
    var reminderHour by remember {
        mutableStateOf(prefs.getInt("reminder_hour", 21))
    }
    var reminderMinute by remember {
        mutableStateOf(prefs.getInt("reminder_minute", 0))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1A1040), Color(0xFF0A0A0F))
                        )
                    )
                    .padding(horizontal = 20.dp)
                    .padding(top = 52.dp, bottom = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E2E))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Settings",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Customize your experience",
                            color = Color(0xFF6C63FF),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // Section label
                Text(
                    text = "NOTIFICATIONS",
                    color = Color(0xFF555570),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Daily reminder toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF12121A))
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔔", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Reminder",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Remind me to log my day",
                                color = Color(0xFF555570),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { enabled ->
                            reminderEnabled = enabled
                            prefs.edit().putBoolean("reminder_enabled", enabled).apply()
                            if (enabled) {
                                NotificationScheduler.scheduleDaily(context, reminderHour, reminderMinute)
                            } else {
                                NotificationScheduler.cancel(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6C63FF),
                            uncheckedThumbColor = Color(0xFF555570),
                            uncheckedTrackColor = Color(0xFF1E1E2E)
                        )
                    )
                }

                if (reminderEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Time picker
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF12121A))
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        reminderHour = hour
                                        reminderMinute = minute
                                        prefs.edit()
                                            .putInt("reminder_hour", hour)
                                            .putInt("reminder_minute", minute)
                                            .apply()
                                        NotificationScheduler.scheduleDaily(context, hour, minute)
                                    },
                                    reminderHour,
                                    reminderMinute,
                                    true
                                ).show()
                            }
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🕐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Reminder Time",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Tap to change",
                                    color = Color(0xFF555570),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = "%02d:%02d".format(reminderHour, reminderMinute),
                            color = Color(0xFF6C63FF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // About section
                Text(
                    text = "ABOUT",
                    color = Color(0xFF555570),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF12121A))
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("📓 Daylog", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("v1.0", color = Color(0xFF555570), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your personal productivity journal. Track tasks, mood, sleep and build better habits.",
                        color = Color(0xFF555570),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF1E1E2E))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Open source • No ads • Your data stays on your device",
                        color = Color(0xFF6C63FF),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}