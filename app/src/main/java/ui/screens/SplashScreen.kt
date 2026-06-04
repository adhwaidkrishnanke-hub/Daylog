package com.eadiat.daylog.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            alpha.animateTo(1f, animationSpec = tween(800, easing = EaseOut))
        }
        launch {
            scale.animateTo(1f, animationSpec = tween(800, easing = EaseOutBack))
        }
        delay(600)
        taglineAlpha.animateTo(1f, animationSpec = tween(600))
        delay(1000)
        launch { alpha.animateTo(0f, animationSpec = tween(500)) }
        delay(500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0F),
                        Color(0xFF1A1040),
                        Color(0xFF0A0A0F)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            Text(text = "📓", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Daylog",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "your day. your story.",
                color = Color(0xFF6C63FF),
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        Text(
            text = "v1.0",
            color = Color(0xFF333333),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(alpha.value)
        )
    }
}