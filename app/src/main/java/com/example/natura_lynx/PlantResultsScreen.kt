package com.example.natura_lynx

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantResultsScreen(navController: NavController, result: String?) {
    val context = LocalContext.current
    val plantRepo = PlantidRepo(context)
    val plantSuggestions = remember { mutableStateListOf<Plant>() }
    val isLoading = remember { mutableStateOf(false) }

    // Launching a coroutine to fetch plant suggestions
    LaunchedEffect(result) {
        if (!result.isNullOrEmpty()) {
            isLoading.value = true
            // Assuming the result is the base64 image or byte array you want to send
            val imageBytes = result.toByteArray() // Convert to byteArray or fetch as needed
            try {
                val plantSuggestionsText = plantRepo.identifyPlant(imageBytes)
                // Process the response and update the plantSuggestions list
                val suggestions = parseSuggestions(plantSuggestionsText) // Parse suggestions here
                plantSuggestions.addAll(suggestions)
            } catch (e: Exception) {
                Log.e("PlantResultsScreen", "Error identifying plant", e)
            }
            isLoading.value = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plant Suggestions") }
            )
        }
    ) {
        if (isLoading.value) {
            // Show loading indicator while fetching data
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(plantSuggestions) { plant ->
                    PlantResultCard(plant = plant, navController = navController)
                }
            }
        }
    }
}

// Function to parse the plant suggestions string and return a list of Plant objects
fun parseSuggestions(responseText: String): List<Plant> {
    val suggestions = mutableListOf<Plant>()
    // Assuming the responseText is formatted as "Suggestion X: <name> with a probability of <probability>"
    val lines = responseText.split("\n")
    for (line in lines) {
        val regex = """Suggestion (\d+): (.*) with a probability of (\d+\.\d{4})""".toRegex()
        val match = regex.find(line)
        match?.let {
            val commonName = it.groupValues[2]
            val probability = it.groupValues[3].toDouble()
            val id = commonName.replace(" ", "_") // Generate a unique ID for the plant
            suggestions.add(Plant(id, commonName, commonName, "image_url_placeholder", probability))
        }
    }
    return suggestions
}
