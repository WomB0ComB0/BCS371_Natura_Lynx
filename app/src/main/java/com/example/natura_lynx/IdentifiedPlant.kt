package com.example.natura_lynx

import kotlinx.serialization.Serializable

@Serializable
data class IdentifiedPlant(
    val id: String,
    val commonName: String,
    val scientificName: String,
    val imageUrl: String,
    val probability: Double = 0.0,
    val family: String = "",
    val genus: String = "",
    val species: String = "",
    val rawIdentification: String = "",
    val additionalDetails: Map<String, String> = emptyMap()
) 