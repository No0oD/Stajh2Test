package com.example.stajh2test.Model.authModel

import androidx.lifecycle.ViewModel
import com.example.stajh2test.ui.states.RegisterUiState
import com.example.stajh2test.ui.states.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Model class for registration UI state management.
 * Handles validation and state updates for the registration screen.
 */
class RegisterModel : ViewModel() {
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

    fun updateRegisterLogin(login: String) {
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
                email = email,
                emailError = ValidationUtils.validateEmail(email)
            )
        }
        validateRegisterForm()
    }

    fun updateRegisterPassword(password: String) {
        _registerUiState.update {
            it.copy(
                password = password,
                passwordError = ValidationUtils.validatePassword(password)
            )
        }

        // Re-validate confirm password if already entered
        val currentState = _registerUiState.value
        if (currentState.confirmPassword.isNotEmpty()) {
            updateConfirmPassword(currentState.confirmPassword)
        }

        validateRegisterForm()
    }

    fun updateConfirmPassword(confirmPassword: String) {
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

    fun toggleRegisterPasswordVisibility() {
        _registerUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _registerUiState.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
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

    fun setLoading(isLoading: Boolean) {
        _registerUiState.update { it.copy(isLoading = isLoading) }
    }

    fun setFirebaseError(error: String?) {
        _registerUiState.update { it.copy(firebaseError = error) }
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

    // Get current form values
    fun getCode() = _registerUiState.value.code
    fun getLogin() = _registerUiState.value.login
    fun getEmail() = _registerUiState.value.email
    fun getPassword() = _registerUiState.value.password
    fun isFormValid() = _registerUiState.value.isFormValid

    fun resetRegisterState() {
        _registerUiState.value = RegisterUiState()
    }
}