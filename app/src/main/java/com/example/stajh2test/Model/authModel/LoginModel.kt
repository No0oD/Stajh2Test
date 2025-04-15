package com.example.stajh2test.Model.authModel

import androidx.lifecycle.ViewModel
import com.example.stajh2test.ui.states.LoginUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Model class for login UI state management.
 * Handles validation and state updates for the login screen.
 */
class LoginModel : ViewModel() {
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun updateEmailField(email: String) {
        _loginUiState.update {
            it.copy(
                email = email.trim(),
                emailError = ValidationUtils.validateEmail(email.trim())
            )
        }
        validateLoginForm()
    }

    fun updateLoginField(email: String) {
        updateEmailField(email)
    }

    fun updatePasswordField(password: String) {
        _loginUiState.update {
            it.copy(
                password = password,
                passwordError = ValidationUtils.validatePassword(password)
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

    fun resetState() {
        _loginUiState.value = LoginUiState()
    }

    // Get current form values
    fun getEmail() = _loginUiState.value.email
    fun getPassword() = _loginUiState.value.password
    fun isFormValid() = _loginUiState.value.isFormValid
}