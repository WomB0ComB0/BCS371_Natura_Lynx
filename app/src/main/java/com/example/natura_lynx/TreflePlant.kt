package com.example.natura_lynx

import kotlinx.serialization.Serializable

@Serializable
data class TreflePlant(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val family: String,
    val genus: String,
    val additionalDetails: Map<String, String>? = null
) 