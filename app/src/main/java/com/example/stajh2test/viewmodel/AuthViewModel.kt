package com.example.stajh2test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Login screen state
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // Register screen state
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    // Current users (in-memory storage for demo)
    private val users = mutableListOf<User>()

    // Login functions
    fun updateLoginField(login: String) {
        _loginUiState.update { it.copy(login = login, loginError = validateLogin(login)) }
        validateLoginForm()
    }

    fun updatePasswordField(password: String) {
        _loginUiState.update { it.copy(password = password, passwordError = validatePassword(password)) }
        validateLoginForm()
    }

    fun togglePasswordVisibility() {
        _loginUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    private fun validateLoginForm() {
        val currentState = _loginUiState.value
        val isFormValid = currentState.loginError == null && 
                          currentState.passwordError == null &&
                          currentState.login.isNotBlank() && 
                          currentState.password.isNotBlank()
        
        _loginUiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun login(): Boolean {
        // For demo purposes, any valid form can log in
        // In a real app, you would check against stored credentials
        return _loginUiState.value.isFormValid
    }

    // Register functions
    fun updateCodeField(code: String) {
        _registerUiState.update { it.copy(code = code, codeError = validateCode(code)) }
        validateRegisterForm()
    }

    fun updateRegisterLogin(login: String) {
        _registerUiState.update { it.copy(login = login, loginError = validateLogin(login)) }
        validateRegisterForm()
    }

    fun updateEmailField(email: String) {
        _registerUiState.update { it.copy(email = email, emailError = validateEmail(email)) }
        validateRegisterForm()
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.update { it.copy(password = password, passwordError = validatePassword(password)) }
        validateRegisterForm()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        val passwordError = if (confirmPassword != _registerUiState.value.password) {
            "Passwords do not match"
        } else null
        
        _registerUiState.update { it.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = passwordError
        )}
        validateRegisterForm()
    }

    fun toggleRegisterPasswordVisibility() {
        _registerUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    private fun validateRegisterForm() {
        val currentState = _registerUiState.value
        val isFormValid = currentState.codeError == null && 
                         currentState.loginError == null && 
                         currentState.emailError == null && 
                         currentState.passwordError == null && 
                         currentState.confirmPasswordError == null &&
                         currentState.code.isNotBlank() && 
                         currentState.login.isNotBlank() && 
                         currentState.email.isNotBlank() && 
                         currentState.password.isNotBlank() && 
                         currentState.confirmPassword.isNotBlank()
        
        _registerUiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun register(): Boolean {
        viewModelScope.launch {
            val state = _registerUiState.value
            if (state.isFormValid) {
                // Add user to our in-memory list
                val newUser = User(
                    code = state.code,
                    login = state.login,
                    email = state.email,
                    password = state.password
                )
                users.add(newUser)
            }
        }
        return _registerUiState.value.isFormValid
    }

    // Validation functions
    private fun validateLogin(login: String): String? {
        return if (login.length < 3) {
            "Login must be at least 3 characters"
        } else null
    }

    private fun validateCode(code: String): String? {
        return if (code.length < 3) {
            "Application code must be at least 3 characters"
        } else null
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return if (!email.matches(emailRegex.toRegex())) {
            "Invalid email format"
        } else null
    }

    private fun validatePassword(password: String): String? {
        return if (password.length < 6) {
            "Password must be at least 6 characters"
        } else null
    }

    // Reset states
    fun resetLoginState() {
        _loginUiState.value = LoginUiState()
    }

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }
}

// Data classes for UI states
data class LoginUiState(
    val login: String = "",
    val loginError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val isFormValid: Boolean = false
)

data class RegisterUiState(
    val code: String = "",
    val codeError: String? = null,
    val login: String = "",
    val loginError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isFormValid: Boolean = false
)
