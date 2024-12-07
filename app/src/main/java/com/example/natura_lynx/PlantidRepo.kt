package com.example.natura_lynx

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

data class PlantIdentification(
    val plantName: String,
    val confidence: Int,
    val commonNames: List<String>,
    val taxonomy: Taxonomy,
    val description: String,
    val wikiUrl: String
)


data class Taxonomy(
    val className: String,
    val genus: String,
    val order: String,
    val family: String,
    val phylum: String,
    val kingdom: String
)

class PlantidRepo(private val context: Context) {

    // OkHttpClient is the client used to make HTTP requests.
    private val client = OkHttpClient()

    // Define the media type for JSON
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // Tag for logging purposes
    private val tag = "PlantIdRepository"

    // Function to send the image for plant identification
    suspend fun identifyPlant(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {

        try {
            // Building the multipart request body
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "images",
                    "image.jpg",
                    RequestBody.create("image/jpeg".toMediaType(), imageBytes)
                )
                .addFormDataPart("api_key", Config.PLANTID_API_KEY)
                .build()

            // Building the HTTP request
            val request = Request.Builder()
                .url("https://api.plant.id/v3")
                .post(requestBody)
                .build()

            // Execute the request asynchronously
            val response = client.newCall(request).execute()

            // Check if the response is successful
            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                // Logging for debugging
                Log.d(tag, "Response code: ${response.code}")
                Log.d(tag, "Response body: $responseBody")

                if (responseBody != null) {
                    val responseJson = JSONObject(responseBody)

                    // Handle the case when the response contains suggestions
                    if (responseJson.has("suggestions")) {
                        val suggestions = responseJson.getJSONArray("suggestions")
                        if (suggestions.length() > 0) {
                            val suggestion = suggestions.getJSONObject(0)
                            val plantName = suggestion.getString("plant_name")
                            val confidence = suggestion.getInt("probability")  // Confidence level
                            val description = suggestion.getString("description")

                            // Log and return more detailed information
                            Log.d(
                                tag,
                                "Plant Name: $plantName, Confidence: $confidence, Description: $description"
                            )

                            // Return a message with plant info
                            return@withContext "Identified plant: $plantName (Confidence: $confidence%)"
                        } else {
                            return@withContext "No suggestions found for this plant."
                        }
                    } else {
                        return@withContext "Error: No suggestions returned from API."
                    }
                } else {
                    return@withContext "Error: Empty response from API."
                }
            } else {
                // Handle different HTTP status codes
                return@withContext when (response.code) {
                    401 -> "Error: Unauthorized request. Please check your API key."
                    500 -> "Error: Server error. Please try again later."
                    else -> "Error: Unable to identify the plant. Status code: ${response.code}"
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error identifying plant", e)
            return@withContext "Failed to identify the plant. Please try again."
        }
    }}