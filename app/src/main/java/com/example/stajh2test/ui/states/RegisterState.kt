package com.example.stajh2test.ui.states


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
    val isFormValid: Boolean = false
)