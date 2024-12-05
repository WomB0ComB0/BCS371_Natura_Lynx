package com.example.natura_lynx

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.natura_lynx.ui.theme.LeafGreen1
import com.example.natura_lynx.ui.theme.LeafGreen2
import androidx.navigation.NavController
import androidx.navigation.Navigation

@Composable
fun LearnScreen(navController: NavController) {
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
            LearnHeader()
            Spacer(modifier = Modifier.height(24.dp))
            SearchBar()
            Spacer(modifier = Modifier.height(24.dp))
            CategoryGrid(navController)
        }
    }
}

@Composable
private fun LearnHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Learn",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Explore the World of Plants",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = { /* TODO: Implement search */ },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search plants and facts...") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun CategoryGrid(navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(category,navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(category: PlantCategory,navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        onClick = { navController.navigate("DetailsScreen") }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            LeafGreen1.copy(alpha = 0.1f),
                            LeafGreen2.copy(alpha = 0.2f)
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
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${category.itemCount} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun getImageForSubcategory(subcategory: String): Int {
    return when (subcategory) {
        "Oak" -> R.drawable.oak
        "Pine" -> R.drawable.pine
        "Maple" -> R.drawable.maple
        "Rose" -> R.drawable.rose
        "Tulip" -> R.drawable.tulip
        "Sunflower" -> R.drawable.sunflower
        "Lilac" -> R.drawable.lilac
        "Hydrangea" -> R.drawable.hydrangea
        "Mushroom" -> R.drawable.mushroom
        "Yeast" -> R.drawable.yeast
        "Peat Moss" -> R.drawable.peat_moss
        "Sphagnum" -> R.drawable.sphagnum
        else -> R.drawable.default_image
    }
}

data class PlantCategory(
    val name: String,
    val icon: ImageVector,
    val itemCount: Int
)

private val categories = listOf(
    PlantCategory("Trees", Icons.Filled.Grass, 25),
    PlantCategory("Flowers", Icons.Filled.Grass, 30),
    PlantCategory("Garden Plants", Icons.Filled.Grass, 20),
    PlantCategory("Herbs", Icons.Filled.Grass, 15)
)