package com.example.stajh2test.ui.states

// Existing state classes
data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val firebaseError: String? = null
)

data class RegisterUiState(
    val code: String = "",
    val codeError: String? = null,
    val login: String = "",
    val loginError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val firebaseError: String? = null
)

// Refactored from ForgotPasswordModel
data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val firebaseError: String? = null
)

// Refactored from VerificationModel
data class VerificationUiState(
    val digit1: String = "",
    val digit2: String = "",
    val digit3: String = "",
    val digit4: String = "",
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

// Refactored from NewPasswordModel
data class NewPasswordUiState(
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val confirmPasswordVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)