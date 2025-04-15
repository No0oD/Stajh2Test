package com.example.stajh2test.Model.resetpassModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.ui.states.ForgotPasswordUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Model class for forgot password UI state management.
 * Handles validation and state updates for the forgot password screen.
 */
class ForgotPasswordModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    // This will be replaced by AuthViewModel with actual implementation
    var onSendVerificationCode: ((String, () -> Unit, (String) -> Unit) -> Unit)? = null

    fun updateEmailField(email: String) {
        _uiState.update {
            it.copy(
                email = email.trim(),
                emailError = ValidationUtils.validateEmail(email.trim()),
                firebaseError = null // Clear any previous errors
            )
        }
        validateForm()
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.emailError == null && currentState.email.isNotBlank()
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun updateFirebaseError(error: String?) {
        _uiState.update { it.copy(firebaseError = error) }
    }

    fun clearErrors() {
        _uiState.update { it.copy(emailError = null, firebaseError = null) }
    }

    // Get current form values
    fun getEmail() = _uiState.value.email
    fun isFormValid() = _uiState.value.isFormValid

    // Method called from UI
    fun sendVerificationCode(onSuccess: () -> Unit) {
        val email = _uiState.value.email

        if (!isFormValid()) {
            return
        }

        updateLoading(true)
        clearErrors()

        onSendVerificationCode?.invoke(
            email,
            {
                updateLoading(false)
                onSuccess()
            },
            { errorMessage ->
                updateLoading(false)
                updateFirebaseError(errorMessage)
            }
        ) ?: run {
            // If the implementation is not provided, just call success
            updateLoading(false)
            onSuccess()
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}