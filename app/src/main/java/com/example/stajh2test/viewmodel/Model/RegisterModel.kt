package com.example.stajh2test.viewmodel.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.model.User
import com.example.stajh2test.ui.states.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterModel : ViewModel() {
    // Реєстраційний стан
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    // Список користувачів (тимчасове сховище)
    private val users = mutableListOf<User>()

    // Оновлення полів
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

    // Перемикання паролів
    fun toggleRegisterPasswordVisibility() {
        _registerUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    // Валідація форми
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

    // Реєстрація
    fun register(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val state = _registerUiState.value
            if (state.isFormValid) {
                users.add(
                    User(
                        code = state.code,
                        login = state.login,
                        email = state.email,
                        password = state.password
                    )
                )
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    // Валідаційні функції
    private fun validateCode(code: String) = when {
        code.length < 3 ->"Application code must be at least 3 characters" else -> null
    }

    private fun validateLogin(login: String) = when {
        login.length < 3 -> "Login must be at least 3 characters" else -> null
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return if (!email.matches(emailRegex.toRegex())) "Invalid email format" else null
    }

    private fun validatePassword(password: String) = when {
        password.length < 6 ->  "Password must be at least 6 characters" else -> null
    }

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }
}