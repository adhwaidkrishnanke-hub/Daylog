package com.eadiat.daylog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.data.DayTaskViewModel

@Composable
fun GoalScreen(
    viewModel: DayTaskViewModel,
    monthYear: String,
    onBack: () -> Unit
) {
    val goal by viewModel.monthGoal.collectAsState()
    var showSetGoalDialog by remember { mutableStateOf(false) }
    var progressInput by remember { mutableStateOf("") }

    LaunchedEffect(monthYear) { viewModel.loadGoal(monthYear) }

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
                            text = "Monthly Goal",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = monthYear,
                            color = Color(0xFF6C63FF),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (goal == null) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎯", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No goal set yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set a monthly goal and track your progress",
                            color = Color(0xFF555570),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { showSetGoalDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Set Goal", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                val progress = goal!!.current.toFloat() / goal!!.target.toFloat().coerceAtLeast(1f)
                val progressClamped = progress.coerceIn(0f, 1f)
                val isCompleted = goal!!.current >= goal!!.target

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    // Goal card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isCompleted)
                                    Brush.linearGradient(listOf(Color(0xFF1A3A1A), Color(0xFF0D2A0D)))
                                else
                                    Brush.linearGradient(listOf(Color(0xFF1A1040), Color(0xFF12121A)))
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isCompleted) "🎉 Goal Achieved!" else "🎯 This Month",
                                    color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFF6C63FF),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal!!.title,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFF2A2A3A),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.deleteGoal(monthYear) }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Big progress number
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${goal!!.current}",
                                color = if (isCompleted) Color(0xFF4CAF50) else Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " / ${goal!!.target} ${goal!!.unit}",
                                color = Color(0xFF555570),
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progressClamped },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFF6C63FF),
                            trackColor = Color(0xFF1E1E2E)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(progressClamped * 100).toInt()}% complete",
                            color = if (isCompleted) Color(0xFF4CAF50) else Color(0xFF6C63FF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Update progress
                    Text(
                        text = "Update Progress",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = progressInput,
                            onValueChange = { progressInput = it },
                            placeholder = { Text("Set progress", color = Color(0xFF555570)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6C63FF),
                                unfocusedBorderColor = Color(0xFF1E1E2E),
                                focusedContainerColor = Color(0xFF12121A),
                                unfocusedContainerColor = Color(0xFF12121A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Button(
                            onClick = {
                                val p = progressInput.toIntOrNull()
                                if (p != null) {
                                    viewModel.updateGoalProgress(monthYear, p)
                                    progressInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Set", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quick add",
                        color = Color(0xFF555570),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf(1, 5, 10).forEach { amount ->
                            OutlinedButton(
                                onClick = {
                                    viewModel.updateGoalProgress(monthYear, goal!!.current + amount)
                                },
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = SolidColor(Color(0xFF6C63FF))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("+$amount", color = Color(0xFF6C63FF), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = { showSetGoalDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change Goal", color = Color(0xFF555570))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showSetGoalDialog) {
        var titleInput by remember { mutableStateOf(goal?.title ?: "") }
        var targetInput by remember { mutableStateOf(goal?.target?.toString() ?: "") }
        var unitInput by remember { mutableStateOf(goal?.unit ?: "") }

        AlertDialog(
            onDismissRequest = { showSetGoalDialog = false },
            containerColor = Color(0xFF12121A),
            title = {
                Text(
                    "Set Monthly Goal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        placeholder = { Text("e.g. Read books", color = Color.Gray) },
                        label = { Text("Goal title", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C63FF),
                            unfocusedBorderColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = targetInput,
                            onValueChange = { targetInput = it },
                            placeholder = { Text("10", color = Color.Gray) },
                            label = { Text("Target", color = Color.Gray) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6C63FF),
                                unfocusedBorderColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = unitInput,
                            onValueChange = { unitInput = it },
                            placeholder = { Text("books", color = Color.Gray) },
                            label = { Text("Unit", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF6C63FF),
                                unfocusedBorderColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val target = targetInput.toIntOrNull() ?: 0
                    if (titleInput.isNotBlank() && target > 0) {
                        viewModel.saveGoal(monthYear, titleInput, target, unitInput)
                        showSetGoalDialog = false
                    }
                }) {
                    Text("Save", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetGoalDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}