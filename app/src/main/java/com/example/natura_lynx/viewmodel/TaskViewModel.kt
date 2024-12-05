class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: State<List<Task>> = _tasks

    init {
        loadInitialTasks()
    }

    private fun loadInitialTasks() {
        val initialTasks = listOf(
            Task(
                title = "Set up initial Android project structure",
                category = TaskCategory.CORE_FEATURES,
                priority = TaskPriority.HIGH
            ),
            Task(
                title = "Implement camera integration",
                category = TaskCategory.CORE_FEATURES,
                priority = TaskPriority.HIGH
            ),
            // Add more tasks from TODO.md
        )
        _tasks.value = initialTasks
    }

    fun toggleTaskCompletion(taskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
            else task
        }
    }
} 