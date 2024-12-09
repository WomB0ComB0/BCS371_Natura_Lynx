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
}

data class Plant(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val probability: Double
)