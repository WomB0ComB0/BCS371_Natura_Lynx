package com.example.natura_lynx

import android.util.Base64
import android.util.Log
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.natura_lynx.ui.theme.LeafGreen1
import com.example.natura_lynx.ui.theme.LeafGreen2
import com.example.natura_lynx.ui.theme.TreeGreen
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun IdentifyScreen(navController: NavController) {
    var plantIdentificationResult by remember { mutableStateOf<String>("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var imageBytes by remember { mutableStateOf<ByteArray>(byteArrayOf()) }
    val context = LocalContext.current

    // Handle image from gallery
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<ByteArray>("selected_image")?.let { bytes ->
            imageBytes = bytes
            savedStateHandle.remove<ByteArray>("selected_image")
        }
    }

    LaunchedEffect(imageBytes) {
        if (imageBytes.isNotEmpty()) {
            scope.launch {
                isLoading = true
                try {
                    val plantRepo = PlantidRepo(context)
                    val result = plantRepo.identifyPlant(imageBytes)
                    
                    // Parse the result JSON
                    val jsonResult = JSONObject(result)
                    val suggestions = jsonResult
                        .getJSONObject("result")
                        .getJSONObject("classification")
                        .getJSONArray("suggestions")

                    if (suggestions.length() > 0) {
                        val topSuggestion = suggestions.getJSONObject(0)
                        val scientificName = topSuggestion.getString("name")
                        val probability = topSuggestion.getDouble("probability")

                        // Get taxonomy from OpenAI
                        val trefleRepo = TrefleRepository()
                        val taxonomy = trefleRepo.getTaxonomyFromOpenAI(scientificName)
                        
                        if (taxonomy != null) {
                            val (familyNullable, genusNullable) = taxonomy
                            val treflePlants = trefleRepo.searchPlants(scientificName)
                            val treflePlant = treflePlants.firstOrNull()

                            if (treflePlant != null) {
                                // Save the scan to recent discoveries
                                val scansManager = RecentScansManager(context)
                                // Convert image bytes to Base64 string
                                val base64Image = Base64.encodeToString(
                                    imageBytes, 
                                    Base64.DEFAULT
                                )
                                val imageUrl = "data:image/jpeg;base64,$base64Image"
                                

                                val family = familyNullable ?: "Unknown Family"
                                val genus = genusNullable ?: "Unknown Genus"
                                
                                val identifiedPlant = IdentifiedPlant(
                                    id = treflePlant.id.toString(),
                                    commonName = treflePlant.commonName,
                                    scientificName = scientificName,
                                    imageUrl = imageUrl,
                                    probability = probability,
                                    family = family,
                                    genus = genus,
                                    species = scientificName.split(" ").getOrNull(1) ?: "",
                                    rawIdentification = result.toString(),
                                    additionalDetails = treflePlant.additionalDetails ?: mapOf(
                                        "Family" to family,
                                        "Genus" to genus
                                    )
                                )
                                scansManager.saveRecentScan(identifiedPlant)
                            }
                        }
                    }
                    
                    navController.currentBackStackEntry?.savedStateHandle?.set("plant_details", result)
                    navController.navigate("plant_details")
                } catch (e: Exception) {
                    Log.e("IdentifyScreen", "Error identifying plant", e)
                    plantIdentificationResult = "Error: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedTreeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedHeader()

            Spacer(modifier = Modifier.height(32.dp))

            CaptureOptions(
                navController = navController,
                onImageCaptured = { capturedImageBytes ->
                    imageBytes = capturedImageBytes
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = plantIdentificationResult,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            RecentIdentifications(navController)
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
fun CaptureOptions(navController: NavController, onImageCaptured: (ByteArray) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CaptureButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.PhotoCamera,
            text = "Take Photo",
            description = "Use camera",
            onClick = { navController.navigate("camera") }
        )

        CaptureButton(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Photo,
            text = "Gallery",
            description = "Choose existing",
            onClick = { 
                Log.d("IdentifyScreen", "Gallery button clicked")
                navController.navigate("gallery") 
            }
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
private fun RecentIdentifications(navController: NavController) {
    val context = LocalContext.current
    val recentScans = remember { mutableStateListOf<IdentifiedPlant>() }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val scansManager = RecentScansManager(context)
                val scans = scansManager.getRecentScans()
                recentScans.clear()
                recentScans.addAll(scans)
            } catch (e: Exception) {
                Log.e("IdentifyScreen", "Error loading recent scans", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Recent Discoveries",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (recentScans.isEmpty()) {
            Text(
                text = "No recent plant identifications",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(recentScans) { scan ->
                    PlantResultCard(
                        plant = scan,
                        navController = navController
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentificationItem(identification: PlantIdentifications) {
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

data class PlantIdentifications(
    val name: String,
    val date: String,
    val confidence: Int
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

    val leavesAngle by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaves_angle"
    )

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