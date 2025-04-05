package com.example.stajh2test.viewmodel.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationUiState(
    val digit1: String = "",
    val digit2: String = "",
    val digit3: String = "",
    val digit4: String = "",
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class VerificationModel : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    fun updateDigit1(digit: String) {
        _uiState.update { it.copy(digit1 = digit) }
        validateForm()
    }

    fun updateDigit2(digit: String) {
        _uiState.update { it.copy(digit2 = digit) }
        validateForm()
    }

    fun updateDigit3(digit: String) {
        _uiState.update { it.copy(digit3 = digit) }
        validateForm()
    }

    fun updateDigit4(digit: String) {
        _uiState.update { it.copy(digit4 = digit) }
        validateForm()
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.digit1.isNotEmpty() &&
                currentState.digit2.isNotEmpty() &&
                currentState.digit3.isNotEmpty() &&
                currentState.digit4.isNotEmpty()
        
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun verifyCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Simulate API call with a delay
                // In a real app, this would verify the code with Firebase or other service
                kotlinx.coroutines.delay(1000)
                
                // For now, we'll accept any 4-digit code as valid
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            }
        }
    }

    fun resetState() {
        _uiState.value = VerificationUiState()
    }
}
