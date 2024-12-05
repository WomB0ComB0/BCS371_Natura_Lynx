package com.example.natura_lynx

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NatureFactViewModel : ViewModel() {
    private val repository = NatureFactRepository()
    private val _natureFact = mutableStateOf<String?>(null)
    private val _isLoading = mutableStateOf(false)
    
    val natureFact: State<String?> = _natureFact
    val isLoading: State<Boolean> = _isLoading

    fun generateNewFact() {
        viewModelScope.launch {
            _isLoading.value = true
            _natureFact.value = repository.getRandomNatureFact()
            _isLoading.value = false
        }
    }
} 