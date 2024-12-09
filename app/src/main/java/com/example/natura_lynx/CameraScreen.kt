package com.example.natura_lynx

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onPhotoTaken: (ByteArray) -> Unit,
    onBack: () -> Unit
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val plantRepo = remember { PlantidRepo(context) }

    val recentScansManager = remember { RecentScansManager(context) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    when {
        cameraPermissionState.status.isGranted -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Camera Preview
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }

                                val imageCaptureInstance = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()
                                imageCapture = imageCaptureInstance

                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview,
                                        imageCaptureInstance
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Failed to bind camera use cases", e)
                                }
                            } catch (e: Exception) {
                                Log.e("CameraScreen", "Failed to get camera provider", e)
                            }
                        }, ContextCompat.getMainExecutor(ctx))

                        previewView
                    }
                )

                // Back button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Capture button
                Button(
                    onClick = {
                        val currentImageCapture = imageCapture
                        if (currentImageCapture != null) {
                            scope.launch {
                                try {
                                    val photoFile = File(
                                        context.cacheDir,
                                        "plant_photo_${System.currentTimeMillis()}.jpg"
                                    )

                                    val outputOptions = ImageCapture.OutputFileOptions
                                        .Builder(photoFile)
                                        .build()

                                    currentImageCapture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                                scope.launch {
                                                    try {
                                                        val imageBytes = photoFile.readBytes()
                                                        val result = plantRepo.identifyPlant(imageBytes)

                                                        // Create IdentifiedPlant from the result
                                                        val json = JSONObject(result)
                                                        val suggestions = json.getJSONObject("result")
                                                            .getJSONObject("classification")
                                                            .getJSONArray("suggestions")
                                                        val firstSuggestion = suggestions.getJSONObject(0)

                                                        // Create and save the identified plant
                                                        val identifiedPlant = IdentifiedPlant(
                                                            id = firstSuggestion.getString("id"),
                                                            commonName = firstSuggestion.getString("name"),
                                                            scientificName = firstSuggestion.getString("name"),
                                                            imageUrl = json.getJSONObject("input")
                                                                .getJSONArray("images")
                                                                .getString(0),
                                                            probability = firstSuggestion.getDouble("probability"),
                                                            family = "",
                                                            genus = "",
                                                            species = "",
                                                            additionalDetails = mapOf()
                                                        )

                                                        // Save to recent scans
                                                        recentScansManager.saveRecentScan(identifiedPlant)
                                                        onPhotoTaken(imageBytes)
                                                    } catch (e: Exception) {
                                                        Log.e("CameraScreen", "Failed to process image", e)
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to process image: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    } finally {
                                                        // Clean up the temporary file
                                                        photoFile.delete()
                                                    }
                                                }
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("CameraScreen", "Failed to capture image", exception)
                                                Toast.makeText(
                                                    context,
                                                    "Failed to capture image: ${exception.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Error taking picture", e)
                                    Toast.makeText(
                                        context,
                                        "Error taking picture: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .size(72.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        cameraPermissionState.status.shouldShowRationale -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Camera permission is needed to take photos")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Request permission")
                }
            }
        }
        else -> {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}
