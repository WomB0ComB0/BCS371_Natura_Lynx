package com.example.natura_lynx

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.json.Json
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import org.json.JSONObject

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantResultsScreen(navController: NavController, result: String?) {
    val context = LocalContext.current
    val trefleRepo = TrefleRepository()
    val plantSuggestions = remember { mutableStateListOf<IdentifiedPlant>() }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(result) {
        if (!result.isNullOrEmpty()) {
            isLoading.value = true
            try {
                Log.d("PlantResultsScreen", "1. Raw result received: $result")
                
                // First check if we can parse the result
                if (result == "null") {
                    throw Exception("No identification result received")
                }

                // Parse the JSON response
                val jsonResult = JSONObject(result)
                Log.d("PlantResultsScreen", "2. Parsed JSON result")

                // Check if we have a valid result object
                if (!jsonResult.has("result")) {
                    throw Exception("Invalid identification response format")
                }

                val suggestions = jsonResult
                    .getJSONObject("result")
                    .getJSONObject("classification")
                    .getJSONArray("suggestions")
                
                Log.d("PlantResultsScreen", "3. Found ${suggestions.length()} suggestions")

                // Get the top suggestion
                if (suggestions.length() > 0) {
                    val topSuggestion = suggestions.getJSONObject(0)
                    val scientificName = topSuggestion.getString("name")
                    val probability = topSuggestion.getDouble("probability")
                    
                    Log.d("PlantResultsScreen", "4. Top suggestion: $scientificName with probability $probability")

                    // Get taxonomy from OpenAI
                    val taxonomy = trefleRepo.getTaxonomyFromOpenAI(scientificName)
                    Log.d("PlantResultsScreen", "5. Taxonomy result: $taxonomy")

                    if (taxonomy != null) {
                        val (family, genus) = taxonomy
                        
                        // Search Trefle
                        val treflePlants = trefleRepo.searchPlants(scientificName)
                        val treflePlant = treflePlants.firstOrNull()

                        if (treflePlant != null) {
                            val identifiedPlant = IdentifiedPlant(
                                id = treflePlant.id.toString(),
                                commonName = treflePlant.commonName,
                                scientificName = scientificName,
                                imageUrl = jsonResult.getJSONObject("input")
                                    .getJSONArray("images")
                                    .getString(0),
                                probability = probability,
                                family = family,
                                genus = genus,
                                species = scientificName.split(" ").getOrNull(1) ?: "",
                                rawIdentification = result,
                                additionalDetails = treflePlant.additionalDetails ?: mapOf(
                                    "Family" to family,
                                    "Genus" to genus
                                )
                            )
                            plantSuggestions.add(identifiedPlant)
                            Log.d("PlantResultsScreen", "6. Added plant to suggestions")
                        } else {
                            Log.e("PlantResultsScreen", "No Trefle plant found for $scientificName")
                        }
                    } else {
                        Log.e("PlantResultsScreen", "No taxonomy returned for $scientificName")
                    }
                } else {
                    throw Exception("No suggestions found in identification result")
                }
            } catch (e: Exception) {
                Log.e("PlantResultsScreen", "Error processing results", e)
                e.printStackTrace()
                Toast.makeText(
                    context,
                    "Error processing identification: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isLoading.value = false
            }
        } else {
            Log.e("PlantResultsScreen", "Received null or empty result")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Identification Results") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (plantSuggestions.isEmpty()) {
                Text(
                    text = "No identification results available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(plantSuggestions) { plant ->
                        PlantResultCard(plant = plant, navController = navController)
                    }
                }
            }
        }
    }
}

