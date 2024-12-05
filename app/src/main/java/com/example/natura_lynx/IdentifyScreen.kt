package com.example.natura_lynx

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.natura_lynx.ui.theme.LeafGreen1
import com.example.natura_lynx.ui.theme.LeafGreen2
import com.example.natura_lynx.ui.theme.TreeGreen

@Composable
fun IdentifyScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Add animated background tree
        AnimatedTreeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Header
            AnimatedHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // Camera and Gallery Buttons with gradient background
            CaptureOptions(navController)

            Spacer(modifier = Modifier.height(40.dp))

            // Recent Identifications with animated entrance
            RecentIdentifications()
        }
    }
}

@Composable
private fun AnimatedHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Identify",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your Plant Discovery Journey",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CaptureOptions(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CaptureButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Add,
            text = "Take Photo",
            description = "Use camera",
            onClick = { navController.navigate("camera") }
        )

        CaptureButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Phone,
            text = "Gallery",
            description = "Choose existing",
            onClick = { navController.navigate("gallery_screen") }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CaptureButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            focusedElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        )
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun RecentIdentifications() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recent Discoveries",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(sampleIdentifications) { identification ->
                IdentificationItem(identification)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentificationItem(identification: PlantIdentification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = identification.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = identification.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${identification.confidence}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// Sample data class and data
data class PlantIdentification(
    val name: String,
    val date: String,
    val confidence: Int
)

private val sampleIdentifications = listOf(
    PlantIdentification("Red Rose", "Today, 2:30 PM", 95),
    PlantIdentification("Oak Tree", "Yesterday", 88),
    PlantIdentification("Sunflower", "2 days ago", 92)
)

@Composable
private fun AnimatedTreeBackground() {
    val treeColor = TreeGreen
    val leafColor1 = LeafGreen1
    val leafColor2 = LeafGreen2

    val infiniteTransition = rememberInfiniteTransition(label = "tree_animation")

    val trunkAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trunk_angle"
    )

    // Leaves swaying animation
    val leavesAngle by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaves_angle"
    )

    // Leaves alpha animation
    val leavesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaves_alpha"
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.25f)
    ) {
        val centerX = size.width / 2
        val treeHeight = size.height * 0.6f
        val trunkWidth = size.width * 0.05f

        // Draw trunk
        rotate(trunkAngle, pivot = androidx.compose.ui.geometry.Offset(centerX, size.height)) {
            drawPath(
                path = Path().apply {
                    moveTo(centerX - trunkWidth / 2, size.height)
                    lineTo(centerX + trunkWidth / 2, size.height)
                    lineTo(centerX + trunkWidth / 3, size.height - treeHeight * 0.7f)
                    lineTo(centerX - trunkWidth / 3, size.height - treeHeight * 0.7f)
                    close()
                },
                color = treeColor.copy(alpha = 0.5f)
            )
        }

        // Draw leaves
        for (i in 0..2) {
            val layerHeight = treeHeight * (0.8f - i * 0.2f)
            val layerWidth = size.width * (0.4f - i * 0.1f)

            rotate(
                degrees = leavesAngle * (1f + i * 0.5f),
                pivot = androidx.compose.ui.geometry.Offset(centerX, size.height - treeHeight * 0.7f)
            ) {
                translate(left = 0f, top = -i * 50f) {
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX, size.height - treeHeight * 0.7f - layerHeight)
                            lineTo(centerX + layerWidth / 2, size.height - treeHeight * 0.7f)
                            lineTo(centerX - layerWidth / 2, size.height - treeHeight * 0.7f)
                            close()
                        },
                        color = (if (i % 2 == 0) leafColor1 else leafColor2)
                            .copy(alpha = leavesAlpha * (1f - i * 0.15f))
                    )
                }
            }
        }


        for (i in 0..5) {
            rotate(
                degrees = leavesAngle * (1f + i * 0.3f),
                pivot = androidx.compose.ui.geometry.Offset(centerX, size.height - treeHeight * 0.4f)
            ) {
                drawCircle(
                    color = leafColor2.copy(alpha = leavesAlpha * 0.4f),
                    radius = 10f,
                    center = androidx.compose.ui.geometry.Offset(
                        x = centerX + (i - 2.5f) * 40f,
                        y = size.height - treeHeight * 0.4f
                    )
                )
            }
        }
    }
}