package com.example.natura_lynx

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(onPhotoTaken: (String) -> Unit, onBack: () -> Unit) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    when {
        cameraPermissionState.status.isGranted -> {
            // Camera permission granted, show camera UI
            CameraContent(
                imageCapture = imageCapture,
                onImageCaptureChange = { imageCapture = it },
                context = context,
                lifecycleOwner = lifecycleOwner,
                onPhotoTaken = onPhotoTaken
            )
        }
        cameraPermissionState.status.shouldShowRationale -> {
            // Show rationale dialog
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
            // Request camera permission
            SideEffect {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
private fun CameraContent(
    imageCapture: ImageCapture?,
    onImageCaptureChange: (ImageCapture) -> Unit,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onPhotoTaken: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
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
                        onImageCaptureChange(imageCaptureInstance)

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
                            // Show error to user
                            Toast.makeText(
                                context,
                                "Failed to initialize camera: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e("CameraScreen", "Failed to get camera provider", e)
                        Toast.makeText(
                            context,
                            "Failed to initialize camera: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        // Capture button
        Button(
            modifier = Modifier
                .padding(16.dp)
                .size(70.dp),
            shape = CircleShape,
            onClick = {
                val currentImageCapture = imageCapture
                if (currentImageCapture != null) {
                    val photoFile = File(
                        context.getExternalFilesDir(null),
                        "${System.currentTimeMillis()}.jpg"
                    )

                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    currentImageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                onPhotoTaken(photoFile.absolutePath)
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
                } else {
                    Toast.makeText(
                        context,
                        "Camera is not ready yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Take photo",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
