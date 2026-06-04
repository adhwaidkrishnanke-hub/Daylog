package com.eadiat.daylog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.data.DayTask
import com.eadiat.daylog.data.DayTaskViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DayScreen(
    viewModel: DayTaskViewModel,
    date: LocalDate,
    onBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    val dayStats by viewModel.dayStats.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<DayTask?>(null) }
    var showSleepDialog by remember { mutableStateOf(false) }
    var showWakeDialog by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    LaunchedEffect(date) { viewModel.selectDate(date) }
    LaunchedEffect(dayStats) { notesText = dayStats?.notes ?: "" }

    val doneCount = tasks.count { it.isDone }
    val total = tasks.size
    val progress = if (total > 0) doneCount.toFloat() / total.toFloat() else 0f
    val isAllDone = total > 0 && doneCount == total

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            item {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1A1040), Color(0xFF0A0A0F))
                            )
                        )
                        .padding(horizontal = 20.dp)
                        .padding(top = 52.dp, bottom = 20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
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
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("EEEE")),
                                    color = Color(0xFF6C63FF),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isAllDone) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text("🎉", fontSize = 24.sp)
                            }
                        }

                        // Progress
                        if (total > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAllDone) "All done! Amazing 🔥" else "$doneCount of $total tasks done",
                                    color = if (isAllDone) Color(0xFF4CAF50) else Color.Gray,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    color = Color(0xFF6C63FF),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (isAllDone) Color(0xFF4CAF50) else Color(0xFF6C63FF),
                                trackColor = Color(0xFF1E1E2E)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sleep / Wake / Mood row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Sleep
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF12121A))
                            .clickable { showSleepDialog = true }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🌙", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (dayStats?.sleepTime.isNullOrEmpty()) "--:--" else dayStats!!.sleepTime,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Sleep", color = Color(0xFF555570), fontSize = 10.sp)
                    }

                    // Wake
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF12121A))
                            .clickable { showWakeDialog = true }
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("☀️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (dayStats?.wakeTime.isNullOrEmpty()) "--:--" else dayStats!!.wakeTime,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Wake", color = Color(0xFF555570), fontSize = 10.sp)
                    }

                    // Mood
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF12121A))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (dayStats?.mood) {
                                1 -> "😞"; 2 -> "😕"; 3 -> "😐"; 4 -> "🙂"; 5 -> "😄"
                                else -> "😶"
                            },
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            listOf(1, 2, 3, 4, 5).forEach { m ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if ((dayStats?.mood ?: 0) >= m)
                                                Color(0xFF6C63FF)
                                            else Color(0xFF2A2A3A)
                                        )
                                        .clickable { viewModel.updateMood(m) }
                                )
                            }
                        }
                        Text("Mood", color = Color(0xFF555570), fontSize = 10.sp)
                    }
                }

                // Tasks header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasks",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (total == 0) {
                        Text(
                            text = "Tap + to add",
                            color = Color(0xFF555570),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Task list
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No tasks yet",
                                color = Color(0xFF555570),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) },
                        onEdit = { editingTask = task }
                    )
                }
            }

            // Notes section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "✍️ Journal",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        placeholder = {
                            Text(
                                "How was your day? Write anything...",
                                color = Color(0xFF333345)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFCCCCCC),
                            focusedBorderColor = Color(0xFF6C63FF),
                            unfocusedBorderColor = Color(0xFF1E1E2E),
                            focusedContainerColor = Color(0xFF12121A),
                            unfocusedContainerColor = Color(0xFF12121A)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        maxLines = 8
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.updateNotes(notesText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E1E2E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Journal Entry", color = Color(0xFF6C63FF))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF6C63FF),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add task", tint = Color.White)
        }
    }

    if (showSleepDialog) {
        TimePickerDialog(
            title = "Sleep Time",
            current = dayStats?.sleepTime ?: "",
            onConfirm = { viewModel.updateSleepTime(it); showSleepDialog = false },
            onDismiss = { showSleepDialog = false }
        )
    }

    if (showWakeDialog) {
        TimePickerDialog(
            title = "Wake Time",
            current = dayStats?.wakeTime ?: "",
            onConfirm = { viewModel.updateWakeTime(it); showWakeDialog = false },
            onDismiss = { showWakeDialog = false }
        )
    }

    if (showAddDialog) {
        var text by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF12121A),
            title = { Text("New Task", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What do you want to do?", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A2A)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (text.isNotBlank()) {
                        viewModel.addTask(text.trim())
                        showAddDialog = false
                    }
                }) { Text("Add", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    editingTask?.let { task ->
        var text by remember { mutableStateOf(task.title) }
        AlertDialog(
            onDismissRequest = { editingTask = null },
            containerColor = Color(0xFF12121A),
            title = { Text("Edit Task", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A2A)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (text.isNotBlank()) {
                        viewModel.editTask(task, text.trim())
                        editingTask = null
                    }
                }) { Text("Save", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingTask = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    title: String,
    current: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(current.split(":").getOrNull(0) ?: "22") }
    var minute by remember { mutableStateOf(current.split(":").getOrNull(1) ?: "00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF12121A),
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = hour,
                    onValueChange = { if (it.length <= 2) hour = it },
                    label = { Text("HH", color = Color.Gray) },
                    modifier = Modifier.width(70.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A2A)
                    )
                )
                Text(
                    " : ",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = minute,
                    onValueChange = { if (it.length <= 2) minute = it },
                    label = { Text("MM", color = Color.Gray) },
                    modifier = Modifier.width(70.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A2A)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm("$hour:$minute") }) {
                Text("Save", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Composable
fun TaskItem(
    task: DayTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF12121A))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (task.isDone) Color(0xFF6C63FF) else Color.Transparent
                )
                .then(
                    if (!task.isDone) Modifier.background(
                        Color.Transparent
                    ) else Modifier
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            if (task.isDone) {
                Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E2E))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = task.title,
            color = if (task.isDone) Color(0xFF555570) else Color.White,
            fontSize = 15.sp,
            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f).clickable { onEdit() }
        )
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color(0xFF2A2A3A),
            modifier = Modifier
                .size(18.dp)
                .clickable { onDelete() }
        )
    }
}