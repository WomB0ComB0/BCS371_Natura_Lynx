package com.example.natura_lynx

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PlantDetailViewModel(
    private val plantId: Int,
    private val repository: TrefleRepository = TrefleRepository()
) : ViewModel() {
    private val _plant = mutableStateOf<TreflePlantDetail?>(null)
    private val _isLoading = mutableStateOf(true)

    val plant: State<TreflePlantDetail?> = _plant
    val isLoading: State<Boolean> = _isLoading

    init {
        loadPlantDetails()
    }

    private fun loadPlantDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _plant.value = repository.getPlantDetails(plantId)
            _isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(plantId: Int): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlantDetailViewModel(plantId) as T
            }
        }
    }
}

//data class TreflePlantDetail(
//    val id: Int,
//    val commonName: String,
//    val scientificName: String,
//    val imageUrl: String,
//    val family: String,
//    val genus: String,
//    val additionalDetails: Map<String, String>? = null
//)