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

class PlantidRepo(private val context: Context) {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val tag = "PlantIdRepository"

    suspend fun identifyPlant(imageBytes: ByteArray): String = withContext(Dispatchers.IO) {
        try {
            // Log the API key and URL
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

                val responseJson = JSONObject(responseBody)
                val result = responseJson.optJSONObject("result")
                if (result != null && result.has("classification")) {
                    val classification = result.getJSONObject("classification")
                    val suggestions = classification.getJSONArray("suggestions")
                    if (suggestions.length() > 0) {
                        val suggestionsList = StringBuilder("Classification Suggestions:\n")
                        for (i in 0 until suggestions.length()) {
                            val suggestion = suggestions.getJSONObject(i)
                            val name = suggestion.getString("name")
                            val probability = suggestion.getDouble("probability")
                            suggestionsList.append("Suggestion ${i + 1}: $name with a probability of ${"%.4f".format(probability)}.\n")
                        }
                        return@withContext suggestionsList.toString().trim()
                    } else {
                        return@withContext "No classification suggestions found for this plant."
                    }
                } else {
                    return@withContext "Error: No classification results found in the response."
                }
            } else {
                Log.e(tag, "API error: ${response.code} - ${responseBody ?: "No response body"}")
                return@withContext "Error: Unable to identify the plant. Status code: ${response.code}"
            }
        } catch (e: Exception) {
            Log.e(tag, "Error identifying plant", e)
            return@withContext "Failed to identify the plant. Please try again."
        }}}

data class Plant(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val probability: Double
)