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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eadiat.daylog.data.DayTaskViewModel

@Composable
fun TemplateScreen(
    viewModel: DayTaskViewModel,
    monthYear: String,
    onBack: () -> Unit
) {
    val templates by viewModel.templateTasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(monthYear) { viewModel.loadTemplate(monthYear) }

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
                            text = "Monthly Template",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Auto-fills every day in $monthYear",
                            color = Color(0xFF6C63FF),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (templates.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No template tasks yet",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add recurring tasks for this month",
                            color = Color(0xFF555570),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Add First Task", color = Color.White)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        Text(
                            text = "${templates.size} recurring task${if (templates.size > 1) "s" else ""}",
                            color = Color(0xFF555570),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(templates) { template ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF12121A))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6C63FF))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = template.title,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFF2A2A3A),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { viewModel.deleteTemplateTask(template) }
                            )
                        }
                    }
                }
            }
        }

        if (templates.isNotEmpty()) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Color(0xFF6C63FF),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    }

    if (showAddDialog) {
        var text by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF12121A),
            title = {
                Text(
                    "Add Template Task",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "This task will appear every day this month",
                        color = Color(0xFF555570),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text("e.g. Morning workout", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C63FF),
                            unfocusedBorderColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (text.isNotBlank()) {
                        viewModel.addTemplateTask(monthYear, text.trim())
                        showAddDialog = false
                    }
                }) {
                    Text("Add", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}