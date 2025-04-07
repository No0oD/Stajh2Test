package com.example.stajh2test.viewmodel.Model

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.stajh2test.ui.states.LoginUiState


class LoginModel : ViewModel() {
    // Login screen state
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // Login functions
    fun updateLoginField(login: String) {
        _loginUiState.update {
            it.copy(
                email = login.trim(),
                emailError = validateLogin(login.trim())
            )
        }
        validateLoginForm()
    }

    fun updatePasswordField(password: String) {
        _loginUiState.update {
            it.copy(
                password = password,
                passwordError = validatePassword(password)
            )
        }
        validateLoginForm()
    }

    fun togglePasswordVisibility() {
        _loginUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun setLoading(isLoading: Boolean) {
        _loginUiState.update { it.copy(isLoading = isLoading) }
    }

    fun setFirebaseError(error: String?) {
        _loginUiState.update { it.copy(firebaseError = error) }
    }

    fun clearErrors() {
        _loginUiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                firebaseError = null
            )
        }
    }

    private fun validateLoginForm() {
        val currentState = _loginUiState.value
        val isFormValid = currentState.emailError == null &&
                currentState.passwordError == null &&
                currentState.email.isNotBlank() &&
                currentState.password.isNotBlank()

        _loginUiState.update { it.copy(isFormValid = isFormValid) }
    }

    private fun validateLogin(login: String) = when {
        login.isEmpty() -> "Email не може бути порожнім"
        !Patterns.EMAIL_ADDRESS.matcher(login).matches() -> "Невірний формат email"
        else -> null
    }

    private fun validatePassword(password: String) = when {
        password.isEmpty() -> "Пароль не може бути порожнім"
        password.length < 6 -> "Пароль має містити мінімум 6 символів"
        else -> null
    }

    fun resetState() {
        _loginUiState.value = LoginUiState()
    }
}