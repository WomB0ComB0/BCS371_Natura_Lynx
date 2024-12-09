package com.example.natura_lynx

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Welcome Header
            Text(
                text = "Welcome to",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "NaturaLynx",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main Action Buttons
            ActionButton(
                icon = Icons.Filled.AddCircle,
                text = "Identify Plants",
                description = "Take a photo to identify plants",
                onClick = { navController.navigate("identify") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButton(
                icon = Icons.Filled.AccountBox,
                text = "Learn About Nature",
                description = "Explore and learn about plants",
                onClick = { navController.navigate("learn") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Stats
            QuickStats()

            // Animated Background
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SwayingLeaves()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun QuickStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = "42", label = "Plants\nIdentified")
        StatItem(count = "15", label = "Nature Facts\nLearned")
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SwayingLeaves() {
    val infiniteTransition = rememberInfiniteTransition(label = "leaves")

    // Natural leaf colors
    val leafGreen1 = Color(0xFF4CAF50)
    val leafGreen2 = Color(0xFF81C784)
    val leafGreen3 = Color(0xFF2E7D32)

    val leavesAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "angle"
    )

    val leavesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val windOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wind"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width * 0.4f

            for (i in 0..3) {
                val layerWidth = size.width * (0.85f - i * 0.1f)
                val layerHeight = size.height * (0.7f - i * 0.08f)

                rotate(
                    degrees = leavesAngle * (1f + i * 0.5f),
                    pivot = Offset(centerX, size.height - layerHeight * 0.5f)
                ) {
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX, size.height - layerHeight)
                            cubicTo(
                                centerX + layerWidth * 0.3f, size.height - layerHeight * 0.8f,
                                centerX + layerWidth * 0.5f, size.height - layerHeight * 0.6f,
                                centerX + layerWidth * 0.5f, size.height - layerHeight * 0.3f
                            )
                            // Left curve
                            cubicTo(
                                centerX + layerWidth * 0.4f, size.height - layerHeight * 0.5f,
                                centerX - layerWidth * 0.3f, size.height - layerHeight * 0.8f,
                                centerX, size.height - layerHeight
                            )
                            close()
                        },
                        color = when (i % 3) {
                            0 -> leafGreen1
                            1 -> leafGreen2
                            else -> leafGreen3
                        }.copy(alpha = leavesAlpha * (1f - i * 0.15f))
                    )
                }
            }
            for (i in 0..15) {
                val progress = (windOffset + i / 15f) % 1f
                val x = size.width * (0.2f + progress * 0.8f)  // Moves from left to right
                val y = size.height * (0.3f + (sin(progress * 6f) * 0.1f))
                val particleAlpha = 0.3f * (1f - progress)  // Fade out as they move

                drawCircle(
                    color = Color.White.copy(alpha = particleAlpha),
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
            for (i in 0..3) {
                val progress = (windOffset + i / 4f) % 1f
                val startX = size.width * (0.1f + progress * 0.7f)

                drawPath(
                    path = Path().apply {
                        moveTo(startX, size.height * 0.4f)
                        quadraticBezierTo(
                            startX + 100f,
                            (size.height * (0.4f + sin(progress * PI) * 0.1f)).toFloat(),
                            startX + 200f, size.height * 0.4f
                        )
                    },
                    color = Color.White.copy(alpha = 0.1f * (1f - progress)),
                    style = Stroke(width = 2f)
                )
            }
            for (i in 0..5) {
                rotate(
                    degrees = leavesAngle * (1f + i * 0.3f),
                    pivot = Offset(centerX, size.height - size.height * 0.4f)
                ) {
                    drawPath(
                        path = Path().apply {
                            val particleSize = 12f
                            moveTo(centerX + (i - 2.5f) * 40f, size.height - size.height * 0.4f)
                            cubicTo(
                                centerX + (i - 2.5f) * 40f + particleSize, size.height - size.height * 0.4f - particleSize,
                                centerX + (i - 2.5f) * 40f + particleSize, size.height - size.height * 0.4f + particleSize,
                                centerX + (i - 2.5f) * 40f, size.height - size.height * 0.4f
                            )
                        },
                        color = leafGreen2.copy(alpha = leavesAlpha * 0.4f)
                    )
                }
            }
        }
    }
}