package com.example.natura_lynx

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.natura_lynx.R

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
                        .padding(vertical = 8.dp)
                        .clickable {
                            expandedStates[category] = expandedStates[category] != true
                        },
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.h6,
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
                                            navController.navigate("details/$subcategory")
                                        },
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(subcategory: String) {
    val info = getInfoForSubcategory(subcategory)
    val imageRes = getImageForSubcategory(subcategory)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = subcategory,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF006400)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = subcategory,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = info,
            style = MaterialTheme.typography.body1
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

// Setup navigation
@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { LearnScreen(navController = navController) }
        composable("details/{subcategory}") { backStackEntry ->
            val subcategory = backStackEntry.arguments?.getString("subcategory") ?: ""
            DetailScreen(subcategory = subcategory)
        }
    }
}
