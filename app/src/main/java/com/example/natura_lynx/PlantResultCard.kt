package com.example.natura_lynx

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantResultCard(plant: IdentifiedPlant, navController: NavController) {
    var showDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (showDetails) 8.dp else 0.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background color if no image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                // Plant image
                if (plant.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = plant.imageUrl,
                        contentDescription = plant.commonName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Gradient overlay and text
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

                        if (plant.probability > 0) {
                            Box {
                                // Shadow layer
                                Text(
                                    text = "Confidence: ${(plant.probability * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.offset(2.dp, 2.dp)
                                )
                                // Main text layer
                                Text(
                                    text = "Confidence: ${(plant.probability * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Button(
                            onClick = { showDetails = !showDetails },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.End)
                        ) {
                            Text(if (showDetails) "Hide Details" else "Show Details")
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showDetails,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Classification Suggestions:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val suggestions = remember(plant.rawIdentification) {
                        try {
                            val jsonResult = JSONObject(plant.rawIdentification)
                            jsonResult
                                .getJSONObject("result")
                                .getJSONObject("classification")
                                .getJSONArray("suggestions")
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (suggestions != null) {
                        for (i in 0 until minOf(suggestions.length(), 3)) {
                            val suggestion = suggestions.getJSONObject(i)
                            val probability = (suggestion.getDouble("probability") * 100).toInt()
                            val name = suggestion.getString("name")

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "$probability%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (i < 2) {  // Don't add divider after last item
                                Divider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Error parsing identification details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (plant.family.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Family: ${plant.family}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    plant.additionalDetails.forEach { (key, value) ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$key: $value",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
