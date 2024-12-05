package com.example.natura_lynx

import ProfileScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.natura_lynx.ui.theme.Natura_lynxTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp

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

    // Check if user is already logged in
    val startDestination = if (auth.currentUser != null) "home" else "login"

    Scaffold(
        bottomBar = {
            if (auth.currentUser != null) {  // Only show bottom nav when logged in
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
                    onPhotoTaken = { photoPath ->
                        // Handle the photo path, e.g., pass it back to IdentifyScreen
                        navController.popBackStack() // Navigate back after photo is taken
                    },
                    onBack = {
                        navController.navigateUp() // Handle back navigation
                    }
                )
            }
            composable("facts") { RandomFactScreen(navController) }
            composable(
                route = "DetailsScreen/{categoryName}",
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                DetailScreen(categoryName, navController)
            }
        }
    }
}