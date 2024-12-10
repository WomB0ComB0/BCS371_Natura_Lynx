package com.example.natura_lynx

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context= LocalContext.current

    val auth = remember { Firebase.auth }

    // Email validation function
    fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email cannot be empty"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password cannot be empty"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            else -> null
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to NaturaLynx",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = validateEmail(it)
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                emailError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = validatePassword(it)
            },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            supportingText = {
                passwordError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                emailError = validateEmail(email)
                passwordError = validatePassword(password)

                if (emailError == null && passwordError == null) {
                    isLoading = true
                    errorMessage = null
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                handleLogin(context, email)
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                errorMessage = task.exception?.message ?: "Authentication failed"
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") }
        ) {
            Text("Don't have an account? Sign up")
        }
    }
}

fun handleLogout(context: Context) {
    val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
    val currentUser = sharedPrefs.getString("current_user", "")
    println("DEBUG: Logging out user: $currentUser")
    
    sharedPrefs.edit()
        .remove("current_user")
        .apply()
}

fun clearUserStats(context: Context, email: String) {
    val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
    println("DEBUG: Clearing stats for user: $email")
    
    sharedPrefs.edit()
        .putInt("fact_count_$email", 0)
        .putInt("plants_identified_$email", 0)
        .apply()
        
    val facts = sharedPrefs.getInt("fact_count_$email", -1)
    val plants = sharedPrefs.getInt("plants_identified_$email", -1)
    println("DEBUG: After clearing - Facts: $facts, Plants: $plants")
}

fun handleLogin(context: Context, email: String, isNewRegistration: Boolean = false) {
    val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
    
    println("DEBUG: Old user was: ${sharedPrefs.getString("current_user", "")}")
    println("DEBUG: New user is: $email")
    println("DEBUG: Is new registration: $isNewRegistration")
    
    if (isNewRegistration) {
        sharedPrefs.edit().clear().apply()
        
        sharedPrefs.edit()
            .putString("current_user", email)
            .putInt("fact_count_$email", 0)
            .putInt("plants_identified_$email", 0)
            .apply()
    }
}

fun printCurrentStats(context: Context) {
    val sharedPrefs = context.getSharedPreferences("NaturaLynx", Context.MODE_PRIVATE)
    val currentUser = sharedPrefs.getString("current_user", "")
    val facts = sharedPrefs.getInt("fact_count_$currentUser", 0)
    val plants = sharedPrefs.getInt("plants_identified_$currentUser", 0)
    println("DEBUG: Current stats for $currentUser - Facts: $facts, Plants: $plants")
}
