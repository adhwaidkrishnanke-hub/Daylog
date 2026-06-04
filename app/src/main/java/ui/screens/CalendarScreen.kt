package com.eadiat.daylog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.data.DayTaskViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    viewModel: DayTaskViewModel,
    onDateClick: (LocalDate) -> Unit,
    onTemplateClick: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val completedDates by viewModel.completedDates.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val today = LocalDate.now()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daylog",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = today.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                        color = Color(0xFF6C63FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                // Settings
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A2E))
                        .clickable { onSettingsClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙️", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionButton(
                    emoji = "🎯",
                    label = "Goal",
                    modifier = Modifier.weight(1f)
                ) { onGoalClick(currentMonth.format(monthFormatter)) }

                QuickActionButton(
                    emoji = "📋",
                    label = "Template",
                    modifier = Modifier.weight(1f)
                ) { onTemplateClick(currentMonth.format(monthFormatter)) }

                QuickActionButton(
                    emoji = "📓",
                    label = "Today",
                    modifier = Modifier.weight(1f),
                    highlighted = true
                ) { onDateClick(today) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calendar card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF12121A))
                    .padding(16.dp)
            ) {
                // Month navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E2E))
                            .clickable { currentMonth = currentMonth.minusMonths(1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("‹", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM")),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentMonth.format(DateTimeFormatter.ofPattern("yyyy")),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E2E))
                            .clickable { currentMonth = currentMonth.plusMonths(1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("›", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day labels
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                        Text(
                            text = it,
                            color = Color(0xFF555570),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                val firstDay = currentMonth.atDay(1)
                val daysInMonth = currentMonth.lengthOfMonth()
                val startOffset = firstDay.dayOfWeek.value % 7
                val totalCells = startOffset + daysInMonth
                val rows = (totalCells + 6) / 7

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNum = cellIndex - startOffset + 1

                            if (dayNum < 1 || dayNum > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val date = currentMonth.atDay(dayNum)
                                val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                val isToday = date == today
                                val isCompleted = completedDates.contains(dateStr)
                                val isPast = date.isBefore(today)
                                val isFuture = date.isAfter(today)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isToday -> Color(0xFF6C63FF)
                                                isCompleted -> Color(0xFF1E4D2B)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable { onDateClick(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayNum.toString(),
                                            color = when {
                                                isToday -> Color.White
                                                isCompleted -> Color(0xFF4CAF50)
                                                isFuture -> Color(0xFF3A3A4A)
                                                else -> Color(0xFF888888)
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (isCompleted && !isToday) {
                                            Text(text = "✓", color = Color(0xFF4CAF50), fontSize = 7.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Month summary
            val completedThisMonth = completedDates.count {
                it.startsWith(currentMonth.format(monthFormatter))
            }
            val daysInMonth = currentMonth.lengthOfMonth()
            val daysPassed = if (currentMonth.year == today.year && currentMonth.monthValue == today.monthValue) {
                today.dayOfMonth
            } else if (currentMonth.isBefore(YearMonth.now())) {
                daysInMonth
            } else 0

            if (daysPassed > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF12121A))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Month Progress",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "$completedThisMonth / $daysPassed days completed",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${if (daysPassed > 0) completedThisMonth * 100 / daysPassed else 0}%",
                        color = Color(0xFF6C63FF),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    emoji: String,
    label: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (highlighted)
                    Brush.linearGradient(listOf(Color(0xFF6C63FF), Color(0xFF9C63FF)))
                else
                    Brush.linearGradient(listOf(Color(0xFF12121A), Color(0xFF1A1A2E)))
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (highlighted) Color.White else Color(0xFF888888),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}