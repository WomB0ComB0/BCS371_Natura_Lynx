
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

@Composable
fun GalleryScreen(navController: NavController, context: Context) {
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var plantIdentificationResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
                    plantIdentificationResult = result
                } catch (e: Exception) {
                    Log.e("GalleryScreen", "Error processing image", e)
                    Toast.makeText(context, "Failed to process image: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }

    if (plantIdentificationResult != null) {
        AlertDialog.Builder(context)
            .setTitle("Plant Identified")
            .setMessage(plantIdentificationResult)
            .setPositiveButton("OK", null)
            .show()
        plantIdentificationResult = null
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