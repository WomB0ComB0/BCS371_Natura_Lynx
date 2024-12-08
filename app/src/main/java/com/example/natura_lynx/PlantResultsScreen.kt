package com.example.natura_lynx

import android.annotation.SuppressLint
import android.util.Base64
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
            try {
                // Assuming result is a base64 string, decode to byteArray
                val imageBytes = Base64.decode(result, Base64.DEFAULT)  // Decode base64 string

                Log.d("PlantResultsScreen", "Sending image to API...")

                val plantSuggestionsText = plantRepo.identifyPlant(imageBytes)

                // Log the raw response
                Log.d("PlantResultsScreen", "API response: $plantSuggestionsText")

                // Process the response and update the plantSuggestions list
                val suggestions = parseSuggestions(plantSuggestionsText)
                Log.d("PlantResultsScreen", "Parsed Suggestions: $suggestions")

                // Add the parsed suggestions to the list
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
            // Log the plantSuggestions list to confirm it's populated
            Log.d("PlantResultsScreen", "Plant Suggestions List: $plantSuggestions")

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
    // Log the raw responseText to check if it is formatted correctly
    Log.d("PlantResultsScreen", "Parsing response: $responseText")

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
    // Log the final list of suggestions to ensure it's correctly populated
    Log.d("PlantResultsScreen", "Final Suggestions List: $suggestions")
    return suggestions
}
