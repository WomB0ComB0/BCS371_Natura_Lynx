@Composable
fun LearnScreen(
    viewModel: LearningViewModel = viewModel()
) {
    val modules by viewModel.learningModules.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(viewModel.selectedTab) {
            Tab(
                selected = viewModel.selectedTab == 0,
                onClick = { viewModel.selectTab(0) }
            ) { Text("Modules") }
            Tab(
                selected = viewModel.selectedTab == 1,
                onClick = { viewModel.selectTab(1) }
            ) { Text("Achievements") }
        }
        
        when (viewModel.selectedTab) {
            0 -> LearningModulesList(modules, viewModel::startModule)
            1 -> AchievementsList(achievements)
        }
    }
} 