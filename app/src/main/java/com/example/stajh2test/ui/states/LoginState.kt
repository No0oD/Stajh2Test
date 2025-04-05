package com.example.stajh2test.ui.states

data class LoginUiState(
    val login: String = "",
    val loginError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val isFormValid: Boolean = false
)