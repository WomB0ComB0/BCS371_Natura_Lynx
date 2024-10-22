package com.example.natura_lynx

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LearnScreen() {
    val natureFactsCategories = listOf("Trees", "Flowers", "Shrubs", "Fungi", "Mosses")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Learn About Nature",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(natureFactsCategories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = 4.dp
                ) {
                    Text(
                        category,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}