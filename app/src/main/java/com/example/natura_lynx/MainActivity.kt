package com.example.natura_lynx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.natura_lynx.ui.theme.Natura_lynxTheme

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

    Scaffold(
        bottomBar = {
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
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen() }
            composable("identify") { IdentifyScreen() }
            composable("learn") { LearnScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}