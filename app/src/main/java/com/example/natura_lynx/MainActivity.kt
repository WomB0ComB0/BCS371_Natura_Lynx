package com.example.natura_lynx

import GalleryScreen
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.natura_lynx.ui.theme.Natura_lynxTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Natura_lynxTheme {
                NaturaLynxApp()
            }
        }
    }
}

@Composable
fun NaturaLynxApp() {
    val navController = rememberNavController()
    val auth = remember { Firebase.auth }
    val context= LocalContext.current
    val scope = CoroutineScope(Dispatchers.IO)
    val startDestination by remember {
        mutableStateOf(if (auth.currentUser != null) "home" else "login")
    }
    val routesWithoutBottomBar = listOf(
        "login",
        "register",
    )

    val shouldShowBottomBar = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route !in routesWithoutBottomBar

    Scaffold(
        bottomBar = {
            if (auth.currentUser != null && shouldShowBottomBar) {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = navController.currentDestination?.route == "home",
                        onClick = { navController.navigate("home") }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Identify") },
                        label = { Text("Identify") },
                        selected = navController.currentDestination?.route == "identify",
                        onClick = { navController.navigate("identify") }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Info, contentDescription = "Learn") },
                        label = { Text("Learn") },
                        selected = navController.currentDestination?.route == "learn",
                        onClick = { navController.navigate("learn") }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = navController.currentDestination?.route == "profile",
                        onClick = { navController.navigate("profile") }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Book, contentDescription = "Facts") },
                        label = { Text("NatureAI") },
                        selected = navController.currentDestination?.route == "facts",
                        onClick = { navController.navigate("facts") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("identify") { IdentifyScreen(navController) }
            composable("learn") { LearnScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("camera") {
                CameraScreen(
                    onPhotoTaken = { imageBytes ->
                        scope.launch {
                            try {
                                val plantRepo = PlantidRepo(context)
                                val result = plantRepo.identifyPlant(imageBytes)
                                val formattedMessage = try {
                                    val json = JSONObject(result)
                                    val suggestions = json.getJSONObject("result")
                                        .getJSONObject("classification")
                                        .getJSONArray("suggestions")

                                    buildString {
                                        appendLine("Identified Plants:")
                                        for (i in 0 until minOf(suggestions.length(), 3)) {
                                            val plant = suggestions.getJSONObject(i)
                                            val probability = (plant.getDouble("probability") * 100).toInt()
                                            appendLine("${i + 1}. ${plant.getString("name")} ($probability%)")
                                        }
                                    }
                                } catch (e: Exception) {
                                    "Error formatting plant details"
                                }

                                withContext(Dispatchers.Main) {
                                    AlertDialog.Builder(context)
                                        .setTitle("Plant Identified")
                                        .setMessage(formattedMessage)
                                        .setPositiveButton("GO TO RESULT") { _, _ ->
                                            Log.d("CameraScreen", "Navigating with identification: $result")
                                            navController.navigate("plant_results/${Uri.encode(result.toString())}")
                                        }
                                        .setNegativeButton("OK") { _, _ ->
                                            navController.popBackStack()
                                        }
                                        .show()
                                }
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error processing image", e)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Error processing image: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigateUp()
                                }
                            }
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }
            composable("gallery") { 
                Log.d("Navigation", "Navigating to gallery screen")
                GalleryScreen(navController, context )
            }
            composable("facts") { RandomFactScreen(navController) }
            composable(
                route = "DetailsScreen/{categoryName}",
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                DetailScreen(categoryName, navController)
            }
            composable(
                route = "plant_detail/{plantId}",
                arguments = listOf(navArgument("plantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
                PlantDetailScreen(
                    plantId = plantId,
                    navController = navController
                )
            }
            composable(
                route = "category_results/{category}",
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                CategoryResultsScreen(category = category, navController = navController)
            }
            composable("plant_results/{result}") { backStackEntry ->
                val result = backStackEntry.arguments?.getString("result")
                PlantResultsScreen(navController,result = result)
            }
        }
    }
}