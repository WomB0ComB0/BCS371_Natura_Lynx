data class LearningModule(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val difficulty: Difficulty,
    val content: List<LearningContent>,
    val quiz: Quiz? = null
)

sealed class LearningContent {
    data class Text(val content: String) : LearningContent()
    data class Image(val url: String, val caption: String) : LearningContent()
    data class Video(val url: String, val duration: Int) : LearningContent()
    data class Interactive(val type: InteractiveType, val data: Map<String, Any>) : LearningContent()
}

data class Quiz(
    val questions: List<Question>,
    val passingScore: Int = 70
)

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
} 