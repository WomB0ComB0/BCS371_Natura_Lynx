interface PlantRecognitionService {
    suspend fun identifyPlant(imageUri: Uri): PlantIdentificationResult
}

class PlantNetService(private val apiKey: String) : PlantRecognitionService {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun identifyPlant(imageUri: Uri): PlantIdentificationResult {
        val file = File(imageUri.path!!)
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "images",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .url("https://my-api.plantnet.org/v2/identify/all")
            .addHeader("Api-Key", apiKey)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("API call failed")
                json.decodeFromString(response.body!!.string())
            }
        }
    }
} 