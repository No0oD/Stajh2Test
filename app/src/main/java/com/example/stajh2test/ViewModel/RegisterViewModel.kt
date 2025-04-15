package com.example.stajh2test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.db.User
import com.example.stajh2test.repository.FirebaseRepository
import com.example.stajh2test.ui.states.RegisterUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState.asStateFlow()

    fun updateCodeField(code: String) {
        _registerUiState.update {
            it.copy(
                code = code,
                codeError = ValidationUtils.validateCode(code)
            )
        }
        validateRegisterForm()
    }

    fun updateLoginField(login: String) {
        _registerUiState.update {
            it.copy(
                login = login,
                loginError = ValidationUtils.validateLogin(login)
            )
        }
        validateRegisterForm()
    }

    fun updateEmailField(email: String) {
        _registerUiState.update {
            it.copy(
                email = email.trim(),
                emailError = ValidationUtils.validateEmail(email.trim())
            )
        }
        validateRegisterForm()
    }

    fun updatePasswordField(password: String) {
        val currentState = _registerUiState.value

        // If confirm password is not empty, validate it against the new password
        val confirmPasswordError = if (currentState.confirmPassword.isNotEmpty()) {
            ValidationUtils.validateConfirmPassword(password, currentState.confirmPassword)
        } else {
            currentState.confirmPasswordError
        }

        _registerUiState.update {
            it.copy(
                password = password,
                passwordError = ValidationUtils.validatePassword(password),
                confirmPasswordError = confirmPasswordError
            )
        }
        validateRegisterForm()
    }

    fun updateConfirmPasswordField(confirmPassword: String) {
        _registerUiState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = ValidationUtils.validateConfirmPassword(
                    _registerUiState.value.password,
                    confirmPassword
                )
            )
        }
        validateRegisterForm()
    }

    fun togglePasswordVisibility() {
        _registerUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    fun registerUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true, firebaseError = null) }

            val currentState = _registerUiState.value
            val user = User(
                code = currentState.code,
                login = currentState.login,
                email = currentState.email,
                password = currentState.password
            )

            val result = repository.registerUser(user)

            result.fold(
                onSuccess = {
                    _registerUiState.update { it.copy(isLoading = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _registerUiState.update {
                        it.copy(
                            isLoading = false,
                            firebaseError = error.message
                        )
                    }
                }
            )
        }
    }

    private fun validateRegisterForm() {
        val currentState = _registerUiState.value
        val isFormValid = currentState.codeError == null &&
                currentState.loginError == null &&
                currentState.emailError == null &&
                currentState.passwordError == null &&
                currentState.confirmPasswordError == null &&
                currentState.code.isNotBlank() &&
                currentState.login.isNotBlank() &&
                currentState.email.isNotBlank() &&
                currentState.password.isNotBlank() &&
                currentState.confirmPassword.isNotBlank()

        _registerUiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun clearErrors() {
        _registerUiState.update {
            it.copy(
                codeError = null,
                loginError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                firebaseError = null
            )
        }
    }

    fun resetState() {
        _registerUiState.value = RegisterUiState()
    }
}