package com.example.natura_lynx

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CategoryResultsViewModel(
    private val context: Context
) : ViewModel() {
    private val repository = TrefleRepository()
    
    private val _plants = MutableStateFlow<List<TreflePlant>>(emptyList())
    val plants: StateFlow<List<TreflePlant>> = _plants.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun loadCachedPlants(category: String): List<TreflePlant>? {
        val prefs = context.getSharedPreferences("plant_cache", Context.MODE_PRIVATE)
        val json = prefs.getString(category, null) ?: return null
        return try {
            Json.decodeFromString<List<TreflePlant>>(json)
        } catch (e: Exception) {
            Log.e("CategoryResults", "Error loading cached plants", e)
            null
        }
    }

    private fun savePlantsToCache(category: String, plants: List<TreflePlant>) {
        val prefs = context.getSharedPreferences("plant_cache", Context.MODE_PRIVATE)
        val json = Json.encodeToString(plants)
        prefs.edit().putString(category, json).apply()
    }

    fun loadPlantsForCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Check disk cache first
                loadCachedPlants(category)?.let { cachedPlants ->
                    _plants.value = cachedPlants
                    _isLoading.value = false
                    return@launch
                }

                // If not in cache, load from API
                val results = repository.searchPlantsByCategory(category)
                savePlantsToCache(category, results)  // Save to disk cache
                _plants.value = results
            } catch (e: Exception) {
                Log.e("CategoryResults", "Error loading plants", e)
                _plants.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
} 