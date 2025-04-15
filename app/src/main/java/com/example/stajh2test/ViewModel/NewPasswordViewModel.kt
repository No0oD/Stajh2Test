package com.example.stajh2test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.repository.FirebaseRepository
import com.example.stajh2test.ui.states.NewPasswordUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewPasswordViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    // Email from the verification step
    private var emailToReset: String = ""

    fun setEmailToReset(email: String) {
        emailToReset = email
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = ValidationUtils.validatePassword(password)
            )
        }

        // Re-validate confirm password
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
                )
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

    fun resetPassword(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val result = repository.resetPassword(emailToReset, _uiState.value.password)

                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }
                    }
                )
            }
        }
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

    fun resetState() {
        _uiState.value = NewPasswordUiState()
    }
}