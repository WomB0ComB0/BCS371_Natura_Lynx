package com.example.natura_lynx

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.natura_lynx.TreflePlant

class PlantSearchViewModel : ViewModel() {
    private val repository = TrefleRepository()
    private val _searchQuery = mutableStateOf("")
    private val _searchResults = mutableStateOf<List<TreflePlant>>(emptyList())
    private val _isLoading = mutableStateOf(false)
    private var searchJob: Job? = null

    val searchQuery: State<String> = _searchQuery
    val searchResults: State<List<TreflePlant>> = _searchResults
    val isLoading: State<Boolean> = _isLoading

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob = viewModelScope.launch {
                delay(500)
                _isLoading.value = true
                _searchResults.value = repository.searchPlants(query)
                _isLoading.value = false
            }
        } else {
            _searchResults.value = emptyList()
        }
    }
}