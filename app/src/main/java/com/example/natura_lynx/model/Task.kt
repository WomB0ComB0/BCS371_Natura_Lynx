data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: TaskCategory,
    val priority: TaskPriority,
    val isCompleted: Boolean = false
)

enum class TaskCategory {
    CORE_FEATURES,
    UI_UX,
    EDUCATIONAL,
    SOCIAL,
    ADDITIONAL,
    OPTIMIZATION,
    PLATFORM,
    SECURITY,
    ANALYTICS,
    DOCUMENTATION
}

enum class TaskPriority {
    HIGH,
    MEDIUM,
    LOW
} 