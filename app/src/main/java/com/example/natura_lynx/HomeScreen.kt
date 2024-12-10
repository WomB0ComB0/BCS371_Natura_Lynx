package com.example.natura_lynx

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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

            QuickStats()

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
    val context = LocalContext.current
    val factCount = remember { mutableStateOf(0) }
    val plantsIdentified = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
        factCount.value = sharedPrefs.getInt("fact_count", 0)
        plantsIdentified.value = sharedPrefs.getInt("plants_identified", 0)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = "${plantsIdentified.value}", label = "Plants\nIdentified")
        StatItem(count = "${factCount.value}", label = "Nature Facts\nLearned")
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


    val leafGreen1 = Color(0xFF4CAF50)
    val leafGreen2 = Color(0xFF81C784)
    val leafGreen3 = Color(0xFF2E7D32)


    val windColor1 = Color(0xFF90CAF9)
    val windColor2 = Color(0xFF64B5F6)

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
            animation = tween(2000, easing = FastOutSlowInEasing),
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

            for (i in 0..25) {
                val progress = (windOffset + i / 25f) % 1f
                val entryProgress = 1f - (progress * 2f).coerceIn(0f, 1f)
                val fadeProgress = progress.coerceIn(0f, 1f)

                val x = size.width * (0.1f + progress * 1.2f)
                val baseY = size.height * (0.3f + (i / 25f) * 0.4f)
                val y = baseY + (sin(progress * 8f) * 30f)

                val particleSize = 6f * entryProgress
                val particleAlpha = 0.8f * entryProgress * (1f - fadeProgress * fadeProgress)

                drawCircle(
                    color = windColor1.copy(alpha = particleAlpha * 0.3f),
                    radius = particleSize * 2.5f,
                    center = Offset(x, y)
                )

                drawCircle(
                    color = windColor2.copy(alpha = particleAlpha),
                    radius = particleSize,
                    center = Offset(x, y)
                )

                for (t in 1..3) {
                    val trailX = x - (t * 15f * entryProgress)

                    drawCircle(
                        color = windColor1.copy(alpha = particleAlpha * (1f - t * 0.3f) * 0.3f),
                        radius = particleSize * (1.5f - t * 0.3f),
                        center = Offset(trailX, y)
                    )

                    drawCircle(
                        color = windColor2.copy(alpha = particleAlpha * (1f - t * 0.3f)),
                        radius = particleSize * (1f - t * 0.3f),
                        center = Offset(trailX, y)
                    )
                }
            }

            for (i in 0..5) {
                val progress = (windOffset + i / 6f) % 1f
                val entryProgress = 1f - (progress * 1.5f).coerceIn(0f, 1f)
                val startX = size.width * (0.1f + progress * 0.8f)

                drawPath(
                    path = Path().apply {
                        moveTo(startX, size.height * 0.4f)
                        quadraticBezierTo(
                            startX + 150f,
                            (size.height * (0.4f + sin(progress * PI) * 0.15f)).toFloat(),
                            startX + 300f,
                            size.height * 0.4f
                        )
                    },
                    color = windColor1.copy(alpha = 0.4f * entryProgress),
                    style = Stroke(
                        width = 6f * entryProgress,
                        cap = StrokeCap.Round
                    )
                )

                drawPath(
                    path = Path().apply {
                        moveTo(startX, size.height * 0.4f)
                        quadraticBezierTo(
                            startX + 150f,
                            (size.height * (0.4f + sin(progress * PI) * 0.15f)).toFloat(),
                            startX + 300f,
                            size.height * 0.4f
                        )
                    },
                    color = windColor2.copy(alpha = 0.5f * entryProgress),
                    style = Stroke(
                        width = 3f * entryProgress,
                        cap = StrokeCap.Round
                    )
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