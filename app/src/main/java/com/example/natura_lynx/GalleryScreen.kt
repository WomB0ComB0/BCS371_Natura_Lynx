
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
import java.io.IOException

@Composable
fun GalleryScreen(navController: NavController, context: Context) {
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var plantIdentificationResult by remember { mutableStateOf<String?>(null) }  // Add this state to store the result
    val scope = rememberCoroutineScope()

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use {
                        it.readBytes()
                    } ?: throw IOException("Failed to read image")

                    val plantRepo = PlantidRepo(context)
                    val result = plantRepo.identifyPlant(bytes)
                    plantIdentificationResult = result  // Store the result here

                } catch (e: Exception) {
                    Log.e("GalleryScreen", "Error processing image", e)
                    Toast.makeText(context, "Failed to process image: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    isLoading = false // Set loading state to false after processing
                }
            }
        }
    }

    // Launch the image picker immediately
    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }

    // If the result is not null, display a Toast with the API result
    if (plantIdentificationResult != null) {
        Toast.makeText(context, "Plant Identified: $plantIdentificationResult", Toast.LENGTH_LONG).show()
        plantIdentificationResult = null  // Reset the result after showing the toast
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