package com.example.natura_lynx

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NatureFactViewModel(
    private val repository: NatureFactRepository
) : ViewModel() {
    private val _natureFact = mutableStateOf<String?>(null)
    private val _isLoading = mutableStateOf(false)
    private val _factCount = mutableStateOf(0)
    
    val natureFact: State<String?> = _natureFact
    val isLoading: State<Boolean> = _isLoading
    val factCount: State<Int> = _factCount

    fun generateNewFact() {
        viewModelScope.launch {
            _isLoading.value = true
            _natureFact.value = repository.getRandomNatureFact()
            _factCount.value += 1
            _isLoading.value = false
            repository.saveFactCount(_factCount.value)
        }
    }

    init {
        viewModelScope.launch {
            _factCount.value = repository.getFactCount()
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NatureFactViewModel(NatureFactRepository(context)) as T
            }
        }
    }
} 