package com.example.natura_lynx.data

data class PlantIdentification(
    val plantName: String,
    val confidence: Int,
    val commonNames: List<String>,
    val description: String,
    val imageUrl: String
) 