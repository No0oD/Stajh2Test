package com.example.stajh2test.viewmodel.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewPasswordUiState(
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val confirmPasswordVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)

class NewPasswordModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    fun updatePassword(password: String) {
        _uiState.update { it.copy(
            password = password,
            passwordError = validatePassword(password)
        )}
        // Re-validate confirm password if it's not empty
        val currentState = _uiState.value
        if (currentState.confirmPassword.isNotEmpty()) {
            validateConfirmPassword(currentState.confirmPassword, password)
        }
        validateForm()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(confirmPassword, _uiState.value.password)
        )}
        validateForm()
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.length < 6 -> "Password must be at least 6 characters"
            !password.any { it.isDigit() } -> "Password must contain at least one digit"
            !password.any { it.isLetter() } -> "Password must contain at least one letter"
            else -> null
        }
    }

    private fun validateConfirmPassword(confirmPassword: String, password: String): String? {
        return if (confirmPassword != password) "Passwords do not match" else null
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.passwordError == null &&
                currentState.confirmPasswordError == null &&
                currentState.password.isNotBlank() &&
                currentState.confirmPassword.isNotBlank()
        
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun submitNewPassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true) }
                
                // Simulate API call with a delay
                // In a real app, this would update the password in Firebase or other service
                kotlinx.coroutines.delay(1000)
                
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }
        }
    }

    fun resetState() {
        _uiState.value = NewPasswordUiState()
    }
}
