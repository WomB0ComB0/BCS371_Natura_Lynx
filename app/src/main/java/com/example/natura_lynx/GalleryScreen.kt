package com.example.natura_lynx

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.ContentUris
import coil.compose.AsyncImage

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val imageUris = remember { mutableStateListOf<Uri>() }

    // Use LaunchedEffect to load images asynchronously
    LaunchedEffect(key1 = context) {
        val uriList = loadImagesFromMediaStore(context)
        imageUris.clear()
        imageUris.addAll(uriList)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // More descriptive and stylized empty state
        if (imageUris.isEmpty()) {
            Text(
                text = "No images found in gallery",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = imageUris,
                    key = { it.toString() } // Provide a unique key for better performance
                ) { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Gallery Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

private fun loadImagesFromMediaStore(context: Context): List<Uri> {
    val imageUris = mutableListOf<Uri>()
    
    // More robust image loading with error handling
    try {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageUris.add(contentUri)
            }
        }
    } catch (e: Exception) {
        // Log the error or handle it appropriately
        e.printStackTrace()
    }
    
    return imageUris
}