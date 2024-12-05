package com.example.natura_lynx

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(navController: NavController) {
    val natureFactsCategories = mapOf(
        "Trees" to listOf("Oak", "Pine", "Maple"),
        "Flowers" to listOf("Rose", "Tulip", "Sunflower"),
        "Shrubs" to listOf("Lilac", "Hydrangea"),
        "Fungi" to listOf("Mushroom", "Yeast"),
        "Mosses" to listOf("Peat Moss", "Sphagnum")
    )
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        natureFactsCategories.forEach { (category, subcategories) ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    onClick = {
                        expandedStates[category] = expandedStates[category] != true
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        if (expandedStates[category] == true) {
                            subcategories.forEach { subcategory ->
                                Text(
                                    text = subcategory,
                                    modifier = Modifier
                                        .padding(start = 16.dp, top = 8.dp)
                                        .clickable {
                                            navController.navigate("detail/$subcategory")
                                        },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    categoryName: String,
    navController: NavController
) {
    val info = getInfoForSubcategory(categoryName)
    val imageRes = getImageForSubcategory(categoryName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text(categoryName) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        try {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = categoryName,
                modifier = Modifier.size(200.dp)
            )
        } catch (e: Exception) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("No image available")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = info,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Sample data function for information
fun getInfoForSubcategory(subcategory: String): String {
    return when (subcategory) {
        "Oak" -> "Oak trees are large trees known for their strength and longevity."
        "Pine" -> "Pine trees are coniferous trees commonly found in cold climates."
        "Maple" -> "Maple trees are known for their distinctive leaves and sap used to make syrup."
        "Rose" -> "Roses are beautiful flowers often associated with love and romance."
        "Tulip" -> "Tulips are spring blooming perennials with showy flowers."
        "Sunflower" -> "Sunflowers are tall plants known for their large, bright yellow flowers."
        "Lilac" -> "Lilacs are shrubs known for their fragrant purple or white flowers."
        "Hydrangea" -> "Hydrangeas are shrubs with large flower heads that can change color based on soil pH."
        "Mushroom" -> "Mushrooms are fungi that come in various shapes and sizes, some of which are edible."
        "Yeast" -> "Yeast is a microscopic fungus used in baking and brewing."
        "Peat Moss" -> "Peat moss is a type of moss used in gardening to improve soil quality."
        "Sphagnum" -> "Sphagnum mosses are important in the formation of peat bogs."
        else -> "Information about $subcategory."
    }
}

// Sample data function for images
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