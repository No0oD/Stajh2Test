package com.example.stajh2test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.util.Patterns
import com.example.stajh2test.ui.states.RegisterUiState

class RegisterModel : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

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
            "Паролі не співпадають"
        } else null

        _registerUiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = passwordError
            )
        }
        validateRegisterForm()
    }

    fun toggleRegisterPasswordVisibility() {
        _registerUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun clearErrors() {
        _registerUiState.update {
            it.copy(
                codeError = null,
                loginError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                firebaseError = null
            )
        }
    }

    fun setLoading(isLoading: Boolean) {
        _registerUiState.update { it.copy(isLoading = isLoading) }
    }

    fun setFirebaseError(error: String?) {
        _registerUiState.update { it.copy(firebaseError = error) }
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

    private fun validateCode(code: String) = when {
        code.isEmpty() -> "Код програми не може бути порожнім"
        code.length < 3 -> "Код повинен містити мінімум 3 символи"
        else -> null
    }

    private fun validateLogin(login: String) = when {
        login.isEmpty() -> "Логін не може бути порожнім"
        login.length < 3 -> "Логін повинен містити мінімум 3 символи"
        else -> null
    }

    private fun validateEmail(email: String) = when {
        email.isEmpty() -> "Email не може бути порожнім"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Невірний формат email"
        else -> null
    }

    private fun validatePassword(password: String) = when {
        password.isEmpty() -> "Пароль не може бути порожнім"
        password.length < 6 -> "Пароль повинен містити мінімум 6 символів"
        else -> null
    }

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }
}