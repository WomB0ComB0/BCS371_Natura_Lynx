package com.example.natura_lynx

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RandomFactScreen(navController: NavController) {
    val viewModel: NatureFactViewModel = viewModel()
    val fact = viewModel.natureFact.value
    val isLoading = viewModel.isLoading.value

    // Generate a fact when the screen first loads
    LaunchedEffect(Unit) {
        if (fact == null) {
            viewModel.generateNewFact()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = fact ?: "Press the button to generate a nature fact",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = { viewModel.generateNewFact() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Generate New Fact")
            }
        }
    }
} 