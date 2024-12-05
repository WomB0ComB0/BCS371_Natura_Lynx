class PlantIdentificationViewModel(
    private val plantRecognitionService: PlantRecognitionService,
    private val plantDao: PlantDao
) : ViewModel() {
    private val _identificationState = MutableStateFlow<IdentificationState>(IdentificationState.Idle)
    val identificationState: StateFlow<IdentificationState> = _identificationState

    val recentIdentifications: StateFlow<List<Plant>> = plantDao.getAllPlants()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun identifyPlant(imageUri: Uri) {
        viewModelScope.launch {
            _identificationState.value = IdentificationState.Loading
            try {
                val result = plantRecognitionService.identifyPlant(imageUri)
                val plant = Plant(
                    id = UUID.randomUUID().toString(),
                    commonName = result.commonName,
                    scientificName = result.scientificName,
                    confidence = result.confidence,
                    imageUri = imageUri.toString()
                )
                plantDao.insertPlant(plant)
                _identificationState.value = IdentificationState.Success(plant)
            } catch (e: Exception) {
                _identificationState.value = IdentificationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class IdentificationState {
    object Idle : IdentificationState()
    object Loading : IdentificationState()
    data class Success(val plant: Plant) : IdentificationState()
    data class Error(val message: String) : IdentificationState()
} 