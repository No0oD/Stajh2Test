package com.example.stajh2test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.repository.FirebaseRepository
import com.example.stajh2test.ui.states.LoginUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

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

    fun loginUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, firebaseError = null) }

            val result = repository.loginUser(
                _loginUiState.value.email,
                _loginUiState.value.password
            )

            result.fold(
                onSuccess = {
                    _loginUiState.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _loginUiState.update {
                        it.copy(
                            isLoading = false,
                            firebaseError = error.message
                        )
                    }
                }
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

    fun clearErrors() {
        _loginUiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                firebaseError = null
            )
        }
    }

    fun resetState() {
        _loginUiState.value = LoginUiState()
    }
}