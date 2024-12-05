package com.example.natura_lynx

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.rememberPermissionState
import androidx.camera.view.PreviewView
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun IdentifyScreen(
    viewModel: PlantIdentificationViewModel = viewModel()
) {
    val identificationState by viewModel.identificationState.collectAsState()
    val recentIdentifications by viewModel.recentIdentifications.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (identificationState) {
            is IdentificationState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is IdentificationState.Success -> {
                val plant = (identificationState as IdentificationState.Success).plant
                PlantIdentificationResult(plant)
            }
            is IdentificationState.Error -> {
                Text(
                    text = (identificationState as IdentificationState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> Unit
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Recent Identifications", style = MaterialTheme.typography.h6)
        LazyColumn {
            items(recentIdentifications) { plant ->
                PlantCard(plant)
            }
        }
    }
}

@Composable
private fun CameraPreview(
    cameraManager: CameraManager,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(LocalContext.current) }
    
    LaunchedEffect(previewView) {
        cameraManager.startCamera(lifecycleOwner, previewView)
    }
    
    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}