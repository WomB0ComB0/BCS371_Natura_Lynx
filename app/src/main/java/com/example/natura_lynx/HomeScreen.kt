package com.example.natura_lynx

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to NaturaLynx", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("identify") }) {
            Text("Start Identifying Plants")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("random_fact") }) {
            Text("Explore Nature Facts")
        }
    }
}