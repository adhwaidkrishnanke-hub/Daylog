package com.eadiat.daylog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eadiat.daylog.data.DayTaskViewModel
import com.eadiat.daylog.ui.screens.CalendarScreen
import com.eadiat.daylog.ui.screens.DayScreen
import com.eadiat.daylog.ui.screens.GoalScreen
import com.eadiat.daylog.ui.screens.SettingsScreen
import com.eadiat.daylog.ui.screens.SplashScreen
import com.eadiat.daylog.ui.screens.StatsScreen
import com.eadiat.daylog.ui.screens.TemplateScreen
import com.eadiat.daylog.ui.theme.DaylogTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DaylogTheme {
                DaylogApp()
            }
        }
    }
}

sealed class Screen {
    object Splash : Screen()
    object Calendar : Screen()
    object Settings : Screen()
    data class Day(val date: LocalDate) : Screen()
    data class Template(val monthYear: String) : Screen()
    data class Goal(val monthYear: String) : Screen()
}

@Composable
fun DaylogApp() {
    val viewModel: DayTaskViewModel = viewModel()
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedTab by remember { mutableStateOf(0) }

    when (val s = screen) {
        is Screen.Splash -> SplashScreen(
            onFinished = { screen = Screen.Calendar }
        )
        is Screen.Day -> DayScreen(
            viewModel = viewModel,
            date = s.date,
            onBack = { screen = Screen.Calendar }
        )
        is Screen.Template -> TemplateScreen(
            viewModel = viewModel,
            monthYear = s.monthYear,
            onBack = { screen = Screen.Calendar }
        )
        is Screen.Goal -> GoalScreen(
            viewModel = viewModel,
            monthYear = s.monthYear,
            onBack = { screen = Screen.Calendar }
        )
        is Screen.Settings -> SettingsScreen(
            onBack = { screen = Screen.Calendar }
        )
        is Screen.Calendar -> {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
                Box(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp)) {
                    if (selectedTab == 0) {
                        CalendarScreen(
                            viewModel = viewModel,
                            onDateClick = { date -> screen = Screen.Day(date) },
                            onTemplateClick = { monthYear -> screen = Screen.Template(monthYear) },
                            onGoalClick = { monthYear -> screen = Screen.Goal(monthYear) },
                            onSettingsClick = { screen = Screen.Settings }
                        )
                    } else {
                        StatsScreen(viewModel = viewModel)
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFF111111))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabItem(
                        icon = "📅",
                        label = "Calendar",
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    TabItem(
                        icon = "📊",
                        label = "Stats",
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                }
            }
        }
    }
}

@Composable
fun TabItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 4.dp)
    ) {
        Text(text = icon, fontSize = 22.sp)
        Text(
            text = label,
            color = if (selected) Color(0xFF6C63FF) else Color.Gray,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}