data class PlantCollection(
    val id: String = UUID.randomUUID().toString(),
    val familyId: String,
    val plants: List<SharedPlant>,
    val collaborators: List<String>
)

data class SharedPlant(
    val plantId: String,
    val sharedBy: String,
    val sharedAt: Long = System.currentTimeMillis(),
    val comments: List<Comment> = emptyList()
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isApproved: Boolean = false
)

class SocialSystem(
    private val plantCollectionDao: PlantCollectionDao,
    private val moderationService: ModerationService,
    private val familyService: FamilyService
) {
    suspend fun sharePlant(plantId: String, familyId: String) {
        val plant = SharedPlant(plantId = plantId, sharedBy = getCurrentUserId())
        plantCollectionDao.addPlantToCollection(familyId, plant)
    }

    suspend fun addComment(plantId: String, content: String) {
        val comment = Comment(userId = getCurrentUserId(), content = content)
        if (moderationService.isContentAppropriate(content)) {
            plantCollectionDao.addComment(plantId, comment.copy(isApproved = true))
        }
    }
} 