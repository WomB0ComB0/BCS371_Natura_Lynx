package com.example.natura_lynx

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class NatureFactRepository {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val tag = "NatureFactRepository"

    suspend fun getRandomNatureFact(): String = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("max_tokens", 150)
                put("temperature", 1.0)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a nature expert. Each time you are asked, provide a fact " +
                            "about a completely different species. Do not mention the corpse flower " +
                            "(Amorphophallus titanum) under any circumstances.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", "Tell me a random fact about any plant or fungi species " +
                            "(except the corpse flower). Choose a completely random species " +
                            "that hasn't been mentioned before. Make it concise and interesting.")
                    })
                })
            }

            Log.d(tag, "Request body: ${requestBody}")

            val request = Request.Builder()
                .url(Config.OPENAI_API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer ${Config.OPENAI_API_KEY}")
                .post(requestBody.toString().toRequestBody(JSON))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d(tag, "Response code: ${response.code}")
            Log.d(tag, "Response body: $responseBody")
            
            if (response.isSuccessful && responseBody != null) {
                val responseJson = JSONObject(responseBody)
                val choices = responseJson.getJSONArray("choices")
                if (choices.length() > 0) {
                    choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()
                } else {
                    "Unable to generate a nature fact: No choices returned"
                }
            } else {
                "Error: Unable to generate a nature fact. Status code: ${response.code}"
            }
        } catch (e: Exception) {
            Log.e(tag, "Error generating fact", e)
            "Error: ${e.message}"
        }
    }
} 