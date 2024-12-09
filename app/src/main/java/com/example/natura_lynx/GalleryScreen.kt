import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.natura_lynx.PlantidRepo
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun GalleryScreen(navController: NavController, context: Context) {
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var plantIdentificationResult by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Function to increment identification count
    fun incrementIdentificationCount() {
        val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
        val currentCount = sharedPrefs.getInt("plants_identified", 0)
        sharedPrefs.edit()
            .putInt("plants_identified", currentCount + 1)
            .apply()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream == null) {
                        Log.e("GalleryScreen", "Input stream is null for URI: $uri")
                        Toast.makeText(context, "Failed to load image.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    val bytes = inputStream.use { it.readBytes() }
                    Log.d("GalleryScreen", "Read ${bytes.size} bytes from the image.")

                    val plantRepo = PlantidRepo(context)
                    val result = plantRepo.identifyPlant(bytes)

                    if (result != null) {
                        Log.d("GalleryScreen", "Received identification result: $result")
                        plantIdentificationResult = result
                        showDialog = true
                        incrementIdentificationCount()
                    } else {
                        Log.e("GalleryScreen", "Identification result was null")
                        Toast.makeText(context, "Failed to identify plant", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Log.e("GalleryScreen", "Error processing image", e)
                    Toast.makeText(context, "Failed to process image: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        } else {
            // User cancelled image selection
            isLoading = false
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }

    if (showDialog && plantIdentificationResult != null) {
        val result = plantIdentificationResult // Local capture
        val formattedMessage = try {
            val json = JSONObject(result)
            val suggestions = json.getJSONObject("result")
                .getJSONObject("classification")
                .getJSONArray("suggestions")

            buildString {
                appendLine("Identified Plants:")
                for (i in 0 until minOf(suggestions.length(), 3)) {
                    val plant = suggestions.getJSONObject(i)
                    val probability = (plant.getDouble("probability") * 100).toInt()
                    appendLine("${i + 1}. ${plant.getString("name")} ($probability%)")
                }
            }
        } catch (e: Exception) {
            "Error formatting plant details"
        }

        AlertDialog.Builder(context)
            .setTitle("Plant Identified")
            .setMessage(formattedMessage)
            .setPositiveButton("GO TO RESULT") { _, _ ->
                Log.d("GalleryScreen", "Navigating with identification: $result")
                navController.navigate("plant_results/${Uri.encode(result)}")
                showDialog = false
            }
            .setNegativeButton("OK") { _, _ ->
                showDialog = false
                navController.popBackStack()
            }
            .setOnDismissListener {
                showDialog = false
            }
            .show()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}