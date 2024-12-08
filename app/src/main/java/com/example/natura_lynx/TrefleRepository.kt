package com.example.natura_lynx

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class TrefleRepository {
    private val client = OkHttpClient()
    private val baseUrl = "https://trefle.io/api/v1"
    private val tag = "TrefleRepository"
    private val openAiClient = OkHttpClient()
    private val apiKey = "YZsqPgTOZXGTWhDGl3OzaMG7sLf04v5hDK8TQrCjjPA"

    private val plantFilters = mapOf(
        "rose" to Pair("Rosaceae", "Rosa"),
        "oak" to Pair("Fagaceae", "Quercus"),
        "pine" to Pair("Pinaceae", "Pinus"),
        "maple" to Pair("Sapindaceae", "Acer"),
        "sunflower" to Pair("Asteraceae", "Helianthus")
    )

    private suspend fun getTaxonomyFromOpenAI(query: String): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Return only the family name and genus (in that order) for the plant "$query" in this exact format:
                Rosaceae,Rosa
                If unsure, return the most common/likely taxonomic classification.
                Do not include any other text or explanation.
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a botanical taxonomy expert. Respond only with family,genus format.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            val request = Request.Builder()
                .url(Config.OPENAI_API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${Config.OPENAI_API_KEY}")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = openAiClient.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d(tag, "OpenAI response: $responseBody")
            
            val openAIResponse = Gson().fromJson(responseBody, OpenAIResponse::class.java)
            val taxonomy = openAIResponse.choices.firstOrNull()?.message?.content?.split(",")
            
            if (taxonomy?.size == 2) {
                Pair(taxonomy[0], taxonomy[1])
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting taxonomy from OpenAI", e)
            null
        }
    }

    suspend fun searchPlants(query: String): List<TreflePlant> = withContext(Dispatchers.IO) {
        try {
            if (query.trim().isEmpty()) {
                return@withContext emptyList()
            }
            
            val taxonomy = getTaxonomyFromOpenAI(query)
            
            val url = if (taxonomy != null) {
                val (family, genus) = taxonomy
                "$baseUrl/plants?token=${Config.TREFLE_API_KEY}&filter[family_name]=$family&filter[genus]=$genus"
            } else {
                // Fallback to Rosaceae if OpenAI fails
                "$baseUrl/plants?token=${Config.TREFLE_API_KEY}&filter[family_name]=Rosaceae"
            }
            
            Log.d(tag, "Making request to URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e(tag, "API request failed with code: ${response.code}")
                Log.e(tag, "Error response: $errorBody")
                return@withContext emptyList()
            }

            val responseBody = response.body?.string()
            val jsonData = JSONObject(responseBody ?: "{}")
            if (!jsonData.has("data")) {
                Log.e(tag, "No data field in response")
                return@withContext emptyList()
            }

            val plantsArray = jsonData.getJSONArray("data")
            Log.d(tag, "Found ${plantsArray.length()} plants in response")
            
            val plants = mutableListOf<TreflePlant>()
            for (i in 0 until plantsArray.length()) {
                val plant = plantsArray.getJSONObject(i)
                plants.add(parsePlantFromJson(plant))
            }
            
            plants
        } catch (e: Exception) {
            Log.e(tag, "Error searching plants", e)
            emptyList()
        }
    }

    suspend fun getPlantDetails(id: Int): TreflePlantDetail? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/plants/$id?token=${Config.TREFLE_API_KEY}")
                .build()

            val response = client.newCall(request).execute()
            val jsonData = JSONObject(response.body?.string() ?: "")
            val plant = jsonData.getJSONObject("data")

            val familyObj = plant.optJSONObject("family")
            val genusObj = plant.optJSONObject("genus")
            
            val cleanFamily = familyObj?.optString("name", "Unknown") ?: plant.optString("family", "Unknown")
            val cleanGenus = genusObj?.optString("name", "Unknown") ?: plant.optString("genus", "Unknown")
            
            // Get common name or use scientific name if none exists
            val commonName = plant.optString("common_name")?.takeIf { it.isNotBlank() && it != "null" }
                ?: plant.getString("scientific_name")

            TreflePlantDetail(
                id = plant.getInt("id"),
                commonName = commonName,
                scientificName = plant.getString("scientific_name"),
                imageUrl = plant.optJSONObject("image_url")?.optString("original") 
                    ?: plant.optString("image_url", ""),
                family = cleanFamily,
                genus = cleanGenus,
                additionalDetails = mapOf(
                    "Edible" to (if (plant.optBoolean("edible", false)) "Yes" else "No"),
                    "First Discovered" to (plant.optString("year", "Unknown")),
                    "Toxicity" to (if (plant.optBoolean("poisonous", false)) "Toxic" else "Non-toxic")
                ).filterValues { it != "Unknown" && it.isNotEmpty() }
            )
        } catch (e: Exception) {
            Log.e(tag, "Error getting plant details", e)
            null
        }
    }

    private fun parsePlantFromJson(plantJson: JSONObject): TreflePlant {
        Log.d(tag, "Raw plant JSON: $plantJson")
        // Get common name, handling both null and empty cases
        val commonName = if (plantJson.isNull("common_name")) {
            ""
        } else {
            plantJson.optString("common_name", "")
        }

        return TreflePlant(
            id = plantJson.getInt("id"),
            commonName = commonName,  // Use our properly handled common name
            scientificName = plantJson.optString("scientific_name", "Species unknown"),
            imageUrl = plantJson.optJSONObject("image_url")?.optString("original") 
                ?: plantJson.optString("image_url", ""),
            family = plantJson.optJSONObject("family")?.optString("name")
                ?: plantJson.optString("family", "Unknown Family"),
            genus = plantJson.optJSONObject("genus")?.optString("name")
                ?: plantJson.optString("genus", "Unknown Genus")
        )
    }

    suspend fun getPlantById(id: Int): TreflePlant? {
        return try {
            val url = "${baseUrl}plants/$id?token=$apiKey"
            Log.d(tag, "Making request to URL: $url")
            
            val response = withContext(Dispatchers.IO) {
                URL(url).readText()
            }
            
            Log.d(tag, "Raw response: $response")
            val jsonObject = JSONObject(response)
            val plantJson = jsonObject.getJSONObject("data")
            parsePlantFromJson(plantJson)
        } catch (e: Exception) {
            Log.e(tag, "Error getting plant by id", e)
            null
        }
    }

    suspend fun searchPlantsByCategory(category: String): List<TreflePlant> = withContext(Dispatchers.IO) {
        try {
            // Reduced number of taxonomy pairs per category
            val taxonomyFilter = when (category.lowercase()) {
                "trees" -> listOf(
                    Pair("Pinaceae", "Pinus"),     // Pine family
                    Pair("Fagaceae", "Quercus")    // Oak family
                )
                "flowers" -> listOf(
                    Pair("Rosaceae", "Rosa"),      // Rose family
                    Pair("Asteraceae", "Aster")    // Daisy family
                )
                "garden plants" -> listOf(
                    Pair("Solanaceae", "Solanum"), // Tomato/potato family
                    Pair("Brassicaceae", "Brassica") // Cabbage family
                )
                "herbs" -> listOf(
                    Pair("Lamiaceae", "Mentha"),    // Mint family
                    Pair("Apiaceae", "Petroselinum") // Parsley family
                )
                else -> emptyList()
            }

            val allPlants = mutableListOf<TreflePlant>()
            
            taxonomyFilter.forEach { (family, genus) ->
                val url = "$baseUrl/plants?token=${Config.TREFLE_API_KEY}&filter[family_name]=$family&filter[genus]=$genus"
                Log.d(tag, "Making request to URL: $url")
                
                val request = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonData = JSONObject(responseBody ?: "{}")
                    if (jsonData.has("data")) {
                        val plantsArray = jsonData.getJSONArray("data")
                        Log.d(tag, "Found ${plantsArray.length()} plants for $family/$genus")
                        
                        for (i in 0 until plantsArray.length()) {
                            val plantJson = plantsArray.getJSONObject(i)
                            allPlants.add(parsePlantFromJson(plantJson))
                        }
                    }
                } else {
                    Log.e(tag, "API request failed for $family/$genus with code: ${response.code}")
                }
            }

            Log.d(tag, "Total plants found for category $category: ${allPlants.size}")
            allPlants
            
        } catch (e: Exception) {
            Log.e(tag, "Error searching plants by category", e)
            emptyList()
        }
    }
}
