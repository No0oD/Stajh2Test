package com.example.stajh2test.viewmodel.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)

class ForgotPasswordModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updateEmailField(email: String) {
        _uiState.update { it.copy(
            email = email,
            emailError = validateEmail(email)
        )}
        validateForm()
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return if (!email.matches(emailRegex.toRegex())) "Invalid email format" else null
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.emailError == null && currentState.email.isNotBlank()
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun sendVerificationCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true) }
                
                // Simulate API call with a delay
                // In a real app, this would be a call to Firebase or other auth service
                kotlinx.coroutines.delay(1000)
                
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}
