package com.example.natura_lynx

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class PlantidRepo(private val context: Context) {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val tag = "PlantIdRepository"

    suspend fun identifyPlant(imageBytes: ByteArray): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(tag, "API Key: ${Config.PLANTID_API_KEY}")
                Log.d(tag, "API URL: ${Config.PLANT_API_URL}")

                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("images", "plant_image.jpg", RequestBody.create("image/jpeg".toMediaType(), imageBytes))
                    .addFormDataPart("api_key", Config.PLANTID_API_KEY)
                    .build()

                Log.d(tag, "Request body created with content type: ${requestBody.contentType()}")

                val request = Request.Builder()
                    .url(Config.PLANT_API_URL)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    Log.d(tag, "API response: $responseBody")
                    responseBody
                } else {
                    Log.e(tag, "API error: ${response.code} - ${responseBody ?: "No response body"}")
                    null
                }
            } catch (e: Exception) {
                Log.e(tag, "Error identifying plant", e)
                null
            }
        }
    }

    suspend fun generatePlantDetails(genus: String, species: String): TreflePlant {
        val prompt = """
            Generate detailed information about the plant $genus $species in this exact format:
            Common Name:
            Scientific Name: $genus $species
            Family:
            Description:
            Only provide the requested information, no additional text.
        """.trimIndent()

        try {
            val requestBody = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are a botanical expert. Provide accurate plant information in the exact format requested.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val request = Request.Builder()
                .url(Config.OPENAI_API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${Config.OPENAI_API_KEY}")
                .post(
                    JSONObject()
                        .put("messages", requestBody)
                        .put("model", "gpt-3.5-turbo")
                        .put("temperature", 0.7)
                        .toString()
                        .toRequestBody("application/json".toMediaType())
                )
                .build()

            val response = withContext(Dispatchers.IO) {
                OkHttpClient().newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody)
                val content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                // Parse the response content
                val lines = content.split("\n")
                var commonName = ""
                var family = ""
                var description = ""

                lines.forEach { line ->
                    when {
                        line.startsWith("Common Name:") -> commonName = line.substringAfter(":").trim()
                        line.startsWith("Family:") -> family = line.substringAfter(":").trim()
                        line.startsWith("Description:") -> description = line.substringAfter(":").trim()
                    }
                }

                return TreflePlant(
                    id = "$genus$species".hashCode(),
                    commonName = commonName.ifEmpty { "$genus $species" },
                    scientificName = "$genus $species",
                    imageUrl = "", // Will be replaced with captured image
                    family = family,
                    genus = genus,
                    additionalDetails = mapOf(
                        "Description" to description
                    )
                )
            } else {
                throw Exception("OpenAI API request failed: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("PlantidRepo", "Error generating plant details", e)
            // Return a basic TreflePlant object with minimal information
            return TreflePlant(
                id = "$genus$species".hashCode(),
                commonName = "$genus $species",
                scientificName = "$genus $species",
                imageUrl = "",
                family = "Unknown",
                genus = genus
            )
        }
    }
}

data class Plant(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val probability: Double
)