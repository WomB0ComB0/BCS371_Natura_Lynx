package com.example.natura_lynx

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IdentifyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Identify Plants", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Open camera */ }) {
            Text("Take a Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Open gallery */ }) {
            Text("Choose from Gallery")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Recent Identifications", style = MaterialTheme.typography.h6)
        // TODO: Add a list of recent identifications
    }
}