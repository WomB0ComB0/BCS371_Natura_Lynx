package com.example.natura_lynx

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User Profile", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Username: NatureLover123", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Plants Identified: 42", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Nature Facts Learned: 78", style = MaterialTheme.typography.body1)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /* TODO: Implement settings */ }) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* TODO: Implement logout */ }) {
            Text("Logout")
        }
    }
}