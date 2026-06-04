package com.eadiat.daylog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.data.DayTaskViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen(viewModel: DayTaskViewModel) {
    val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val displayFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthYear = currentMonth.format(monthFormatter)

    val monthStats by viewModel.monthStats.collectAsState()
    val completedDates by viewModel.completedDatesMonth.collectAsState()
    val totalDates by viewModel.totalDatesMonth.collectAsState()

    LaunchedEffect(currentMonth) { viewModel.loadMonthStats(monthYear) }

    val productivityRate = if (totalDates.isEmpty()) 0f
    else completedDates.size.toFloat() / totalDates.size.toFloat() * 100f

    val streak = calculateStreak(completedDates)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
            .verticalScroll(rememberScrollState())
    ) {
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
            Column {
                Text(
                    text = "Stats",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Track your progress",
                    color = Color(0xFF6C63FF),
                    fontSize = 13.sp
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            // Month navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF12121A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E2E))
                        .clickableNoRipple { currentMonth = currentMonth.minusMonths(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("‹", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = currentMonth.format(displayFormatter),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E2E))
                        .clickableNoRipple { currentMonth = currentMonth.plusMonths(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("›", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Top stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Productivity
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF1A1040), Color(0xFF2A1060))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${productivityRate.toInt()}%",
                        color = Color(0xFF6C63FF),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Productivity", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = "${completedDates.size}/${totalDates.size} days",
                        color = Color(0xFF555570),
                        fontSize = 10.sp
                    )
                }

                // Streak
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2A1000), Color(0xFF401800))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$streak",
                        color = Color(0xFFFF6B35),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Day Streak 🔥", color = Color.Gray, fontSize = 11.sp)
                    Text(
                        text = if (streak > 0) "Keep going!" else "Start today",
                        color = Color(0xFF555570),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF12121A))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Monthly Progress",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${productivityRate.toInt()}%",
                        color = Color(0xFF6C63FF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { productivityRate / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF6C63FF),
                    trackColor = Color(0xFF1E1E2E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${completedDates.size} days fully completed out of ${totalDates.size} active days",
                    color = Color(0xFF555570),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood graph card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF12121A))
                    .padding(20.dp)
            ) {
                Text(
                    text = "😊 Mood This Month",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                if (monthStats.filter { it.mood > 0 }.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("😶", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No mood data yet", color = Color(0xFF555570), fontSize = 13.sp)
                        }
                    }
                } else {
                    MoodGraph(monthStats = monthStats.filter { it.mood > 0 }.sortedBy { it.date })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleep card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF12121A))
                    .padding(20.dp)
            ) {
                Text(
                    text = "🌙 Sleep Overview",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                val sleepData = monthStats.filter { it.sleepTime.isNotEmpty() }
                if (sleepData.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌙", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No sleep data yet", color = Color(0xFF555570), fontSize = 13.sp)
                        }
                    }
                } else {
                    sleepData.takeLast(7).forEach { stat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E1E2E))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Day ${stat.date.substring(8)}",
                                    color = Color(0xFF6C63FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "🌙 ${stat.sleepTime}  ☀️ ${stat.wakeTime.ifEmpty { "--:--" }}",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MoodGraph(monthStats: List<com.eadiat.daylog.data.DayStats>) {
    val maxMood = 5f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        monthStats.forEach { stat ->
            val heightFraction = stat.mood / maxMood
            val color = when (stat.mood) {
                1 -> Color(0xFFEF5350)
                2 -> Color(0xFFFF7043)
                3 -> Color(0xFFFFCA28)
                4 -> Color(0xFF66BB6A)
                5 -> Color(0xFF42A5F5)
                else -> Color.Gray
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(heightFraction)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(color)
                )
                Text(
                    text = stat.date.substring(8),
                    color = Color(0xFF333345),
                    fontSize = 7.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            "😞" to Color(0xFFEF5350),
            "😕" to Color(0xFFFF7043),
            "😐" to Color(0xFFFFCA28),
            "🙂" to Color(0xFF66BB6A),
            "😄" to Color(0xFF42A5F5)
        ).forEach { (emoji, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = emoji, fontSize = 11.sp)
            }
        }
    }
}

fun calculateStreak(completedDates: List<String>): Int {
    if (completedDates.isEmpty()) return 0
    val sorted = completedDates.sorted().reversed()
    val today = java.time.LocalDate.now()
    var streak = 0
    var checkDate = today
    for (dateStr in sorted) {
        val date = java.time.LocalDate.parse(dateStr)
        if (date == checkDate) {
            streak++
            checkDate = checkDate.minusDays(1)
        } else if (date.isBefore(checkDate)) {
            break
        }
    }
    return streak
}

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}