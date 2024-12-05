data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val points: Int,
    val icon: String,
    val isUnlocked: Boolean = false
)

data class Badge(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val imageUrl: String,
    val requirementDescription: String,
    val isEarned: Boolean = false
)

class GamificationSystem(
    private val userDao: UserDao,
    private val achievementDao: AchievementDao
) {
    suspend fun awardPoints(userId: String, points: Int, reason: String) {
        userDao.updateUserPoints(userId, points)
        checkAchievements(userId)
    }

    private suspend fun checkAchievements(userId: String) {
        val user = userDao.getUser(userId)
        val achievements = achievementDao.getAchievements()
        
        achievements.forEach { achievement ->
            if (!achievement.isUnlocked && meetsRequirements(user, achievement)) {
                achievementDao.unlockAchievement(achievement.id)
                userDao.addBadge(userId, achievement.id)
            }
        }
    }
} 