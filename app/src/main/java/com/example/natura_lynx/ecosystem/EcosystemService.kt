class EcosystemService(
    private val weatherApi: WeatherApi,
    private val ecosystemDao: EcosystemDao
) {
    suspend fun getLocalEcosystemInfo(latitude: Double, longitude: Double): EcosystemInfo {
        val weather = weatherApi.getCurrentWeather(latitude, longitude)
        val seasonalPlants = getSuggestedPlants(weather.season)
        val localSpecies = ecosystemDao.getLocalSpecies(latitude, longitude)
        
        return EcosystemInfo(
            weather = weather,
            seasonalPlants = seasonalPlants,
            localSpecies = localSpecies
        )
    }

    suspend fun getSeasonalRecommendations(): List<Plant> {
        val currentSeason = getCurrentSeason()
        return ecosystemDao.getPlantsBySeason(currentSeason)
    }
} 