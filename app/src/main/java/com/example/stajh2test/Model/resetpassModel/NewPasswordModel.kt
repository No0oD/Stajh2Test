package com.example.stajh2test.Model.resetpassModel

import androidx.lifecycle.ViewModel
import com.example.stajh2test.ui.states.NewPasswordUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Model class for new password UI state management.
 * Handles validation and state updates for the new password screen.
 */
class NewPasswordModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    // This will be replaced by AuthViewModel with actual implementation
    var onSubmitNewPassword: ((String, () -> Unit, (String) -> Unit) -> Unit)? = null

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = ValidationUtils.validatePassword(password),
                error = null
            )
        }

        // Re-validate confirm password if it's not empty
        val currentState = _uiState.value
        if (currentState.confirmPassword.isNotEmpty()) {
            updateConfirmPassword(currentState.confirmPassword)
        }

        validateForm()
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = ValidationUtils.validateConfirmPassword(
                    _uiState.value.password,
                    confirmPassword
                ),
                error = null
            )
        }
        validateForm()
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.passwordError == null &&
                currentState.confirmPasswordError == null &&
                currentState.password.isNotBlank() &&
                currentState.confirmPassword.isNotBlank()

        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun updateError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    fun clearErrors() {
        _uiState.update {
            it.copy(
                passwordError = null,
                confirmPasswordError = null,
                error = null
            )
        }
    }

    // Get current form values
    fun getPassword() = _uiState.value.password
    fun isFormValid() = _uiState.value.isFormValid

    // Method called from UI
    fun submitNewPassword(onSuccess: () -> Unit) {
        if (!isFormValid()) {
            return
        }

        val password = getPassword()
        updateLoading(true)
        clearErrors()

        onSubmitNewPassword?.invoke(
            password,
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
        _uiState.value = NewPasswordUiState()
    }
}