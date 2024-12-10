package com.example.natura_lynx

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantId: Int,
    private val context: Context
) : ViewModel() {
    private val repository = TrefleRepository()
    
    private val _plant = mutableStateOf<TreflePlant?>(null)
    val plant: State<TreflePlant?> = _plant

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadPlant()
    }

    private fun loadPlant() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val plantDetails = repository.getPlantDetails(plantId)
                plantDetails?.let { details ->
                    _plant.value = TreflePlant(
                        id = details.id,
                        commonName = details.commonName,
                        scientificName = details.scientificName,
                        imageUrl = details.imageUrl,
                        family = details.family,
                        genus = details.genus,
                        additionalDetails = details.additionalDetails
                    )
                }
            } catch (e: Exception) {
                Log.e("PlantDetail", "Error loading plant details", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

