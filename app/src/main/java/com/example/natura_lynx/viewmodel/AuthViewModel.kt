class AuthViewModel(
    private val authService: AuthService,
    private val familyService: FamilyService
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    val currentUser: StateFlow<User?> = authService.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authService.signIn(email, password)
                if (result.success) {
                    _authState.value = AuthState.Authenticated(result.user!!)
                } else {
                    _authState.value = AuthState.Error(result.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signUp(email: String, password: String, userType: UserType) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authService.signUp(email, password, userType)
                if (result.success) {
                    _authState.value = AuthState.Authenticated(result.user!!)
                } else {
                    _authState.value = AuthState.Error(result.error ?: "Unknown error")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
} 