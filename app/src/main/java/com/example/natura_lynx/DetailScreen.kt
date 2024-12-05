package com.example.natura_lynx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.natura_lynx.ui.theme.LeafGreen1
import com.example.natura_lynx.ui.theme.LeafGreen2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    categoryName: String,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text(categoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CategoryDescription(categoryName)
                }

                items(getCategoryItems(categoryName)) { item ->
                    PlantItemCard(item)
                }
            }
        }
    }
}

@Composable
private fun CategoryDescription(categoryName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "About $categoryName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getCategoryDescription(categoryName),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlantItemCard(item: PlantItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = { /* TODO: Navigate to plant detail */ }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            LeafGreen1.copy(alpha = 0.1f),
                            LeafGreen2.copy(alpha = 0.2f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plant info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.scientificName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.shortDescription,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Difficulty indicator
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (item.difficulty) {
                            PlantDifficulty.EASY -> MaterialTheme.colorScheme.primaryContainer
                            PlantDifficulty.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                            PlantDifficulty.HARD -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.difficulty.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = when (item.difficulty) {
                            PlantDifficulty.EASY -> MaterialTheme.colorScheme.onPrimaryContainer
                            PlantDifficulty.MEDIUM -> MaterialTheme.colorScheme.onSecondaryContainer
                            PlantDifficulty.HARD -> MaterialTheme.colorScheme.onTertiaryContainer
                        }
                    )
                }
            }
        }
    }
}

enum class PlantDifficulty {
    EASY, MEDIUM, HARD
}

data class PlantItem(
    val name: String,
    val scientificName: String,
    val shortDescription: String,
    val difficulty: PlantDifficulty
)

private fun getCategoryDescription(categoryName: String): String {
    return when (categoryName) {
        "Trees" -> "Discover the majestic world of trees, from towering evergreens to flowering species."
        "Flowers" -> "Explore beautiful flowering plants that add color and fragrance to our world."
        "Garden Plants" -> "Learn about plants perfect for your garden, including vegetables and ornamentals."
        "Herbs" -> "Discover aromatic and medicinal plants used for cooking and traditional remedies."
        else -> "Explore various plants in this category."
    }
}

private fun getCategoryItems(categoryName: String): List<PlantItem> {
    return when (categoryName) {
        "Trees" -> listOf(
            PlantItem(
                "Oak Tree",
                "Quercus",
                "Majestic hardwood tree known for its strength and longevity",
                PlantDifficulty.MEDIUM
            ),
            PlantItem(
                "Maple Tree",
                "Acer",
                "Beautiful tree with distinctive leaf shapes and fall colors",
                PlantDifficulty.EASY
            ),
            PlantItem(
                "Pine Tree",
                "Pinus",
                "Evergreen conifer with needle-like leaves",
                PlantDifficulty.HARD
            )
        )
        "Flowers" -> listOf(
            PlantItem(
                "Rose",
                "Rosa",
                "Classic flowering plant known for its beauty and fragrance",
                PlantDifficulty.MEDIUM
            ),
            PlantItem(
                "Sunflower",
                "Helianthus",
                "Tall annual with large, bright yellow flowers",
                PlantDifficulty.EASY
            ),
            PlantItem(
                "Orchid",
                "Orchidaceae",
                "Exotic flowering plant with complex blooms",
                PlantDifficulty.HARD
            )
        )
        "Garden Plants" -> listOf(
            PlantItem(
                "Tomato",
                "Solanum lycopersicum",
                "Popular garden vegetable with edible fruits",
                PlantDifficulty.MEDIUM
            ),
            PlantItem(
                "Lettuce",
                "Lactuca sativa",
                "Easy-to-grow leafy vegetable",
                PlantDifficulty.EASY
            ),
            PlantItem(
                "Pepper",
                "Capsicum",
                "Versatile plant with fruits varying in heat levels",
                PlantDifficulty.MEDIUM
            )
        )
        "Herbs" -> listOf(
            PlantItem(
                "Basil",
                "Ocimum basilicum",
                "Aromatic herb used in cooking",
                PlantDifficulty.EASY
            ),
            PlantItem(
                "Rosemary",
                "Rosmarinus officinalis",
                "Fragrant Mediterranean herb",
                PlantDifficulty.MEDIUM
            ),
            PlantItem(
                "Lavender",
                "Lavandula",
                "Fragrant flowering herb with calming properties",
                PlantDifficulty.HARD
            )
        )
        else -> emptyList()
    }
} 