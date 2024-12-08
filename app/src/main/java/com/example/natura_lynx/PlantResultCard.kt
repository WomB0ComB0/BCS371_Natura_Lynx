package com.example.natura_lynx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantResultCard(plant: Plant, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        onClick = {
            navController.navigate("plant_results/${plant.id}")
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = plant.imageUrl,
                contentDescription = plant.commonName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Add semi-transparent overlay at the bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Box {
                        // Shadow layer
                        Text(
                            text = plant.commonName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.offset(2.dp, 2.dp)
                        )
                        // Main text layer
                        Text(
                            text = plant.commonName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box {
                        // Shadow layer
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.offset(2.dp, 2.dp)
                        )
                        // Main text layer
                        Text(
                            text = plant.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Optionally display the probability
                    Text(
                        text = "Probability: ${"%.2f".format(plant.probability * 100)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
