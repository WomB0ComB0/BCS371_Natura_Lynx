package com.example.natura_lynx

data class TreflePlantDetail(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val family: String,
    val genus: String,
    val additionalDetails: Map<String, String>
)