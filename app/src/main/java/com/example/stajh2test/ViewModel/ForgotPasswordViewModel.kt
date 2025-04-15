package com.example.stajh2test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.repository.FirebaseRepository
import com.example.stajh2test.ui.states.ForgotPasswordUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updateEmailField(email: String) {
        _uiState.update {
            it.copy(
                email = email.trim(),
                emailError = ValidationUtils.validateEmail(email.trim())
            )
        }
        validateForm()
    }

    fun sendVerificationCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true, firebaseError = null) }

                val result = repository.sendVerificationCode(_uiState.value.email)

                result.fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                firebaseError = error.message
                            )
                        }
                    }
                )
            }
        }
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.emailError == null && currentState.email.isNotBlank()
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun clearErrors() {
        _uiState.update {
            it.copy(
                emailError = null,
                firebaseError = null
            )
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }
}