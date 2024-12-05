class OfflineManager(
    private val plantDao: PlantDao,
    private val learningContentDao: LearningContentDao,
    private val workManager: WorkManager
) {
    fun enableOfflineMode() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncWork)
    }

    suspend fun syncLocalData() {
        // Sync plants
        val localPlants = plantDao.getAllPlants()
        val remoteChanges = plantDao.getRemoteChanges()
        plantDao.syncPlants(localPlants, remoteChanges)

        // Sync learning content
        val localContent = learningContentDao.getAllContent()
        val remoteContent = learningContentDao.getRemoteContent()
        learningContentDao.syncContent(localContent, remoteContent)
    }
} 