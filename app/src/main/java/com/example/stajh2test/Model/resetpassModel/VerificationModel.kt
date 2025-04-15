package com.example.stajh2test.Model.resetpassModel

import androidx.lifecycle.ViewModel
import com.example.stajh2test.ui.states.VerificationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Model class for verification code UI state management.
 * Handles validation and state updates for the verification screen.
 */
class VerificationModel : ViewModel() {
    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    // This will be replaced by AuthViewModel with actual implementation
    var onVerifyCode: ((String, () -> Unit, (String) -> Unit) -> Unit)? = null

    fun updateDigit1(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit1 = digit, error = null) }
            validateForm()
        }
    }

    fun updateDigit2(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit2 = digit, error = null) }
            validateForm()
        }
    }

    fun updateDigit3(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit3 = digit, error = null) }
            validateForm()
        }
    }

    fun updateDigit4(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit4 = digit, error = null) }
            validateForm()
        }
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.digit1.isNotEmpty() &&
                currentState.digit2.isNotEmpty() &&
                currentState.digit3.isNotEmpty() &&
                currentState.digit4.isNotEmpty()

        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun updateError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    // Get the full verification code
    fun getCode(): String {
        return with(_uiState.value) {
            digit1 + digit2 + digit3 + digit4
        }
    }

    fun isFormValid() = _uiState.value.isFormValid

    // Method called from UI
    fun verifyCode(onSuccess: () -> Unit) {
        if (!isFormValid()) {
            return
        }

        val code = getCode()
        updateLoading(true)

        onVerifyCode?.invoke(
            code,
            {
                updateLoading(false)
                onSuccess()
            },
            { errorMessage ->
                updateLoading(false)
                updateError(errorMessage)
            }
        ) ?: run {
            // If the implementation is not provided, just call success
            updateLoading(false)
            onSuccess()
        }
    }

    fun resetState() {
        _uiState.value = VerificationUiState()
    }
}