interface AuthService {
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String, userType: UserType): AuthResult
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
}

data class AuthResult(
    val success: Boolean,
    val user: User? = null,
    val error: String? = null
)

data class User(
    val id: String,
    val email: String,
    val userType: UserType,
    val familyId: String? = null
)

enum class UserType {
    PARENT,
    CHILD
} 