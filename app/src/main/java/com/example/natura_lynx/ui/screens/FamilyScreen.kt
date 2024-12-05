@Composable
fun FamilyScreen(
    viewModel: FamilyViewModel = viewModel()
) {
    val familyCollection by viewModel.familyCollection.collectAsState()
    val familyMembers by viewModel.familyMembers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Family Collection", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            item {
                FamilyMembersList(familyMembers)
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            }
            
            items(familyCollection.plants) { plant ->
                SharedPlantCard(
                    plant = plant,
                    onComment = { viewModel.addComment(plant.plantId, it) }
                )
            }
        }
    }
} 