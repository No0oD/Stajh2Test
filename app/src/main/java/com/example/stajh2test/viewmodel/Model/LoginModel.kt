package com.example.stajh2test.viewmodel.Model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.stajh2test.ui.states.LoginUiState

class LoginModel: ViewModel() {

    // Login screen state
    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    // Login functions
    fun updateLoginField(login: String) {
        _loginUiState.update { it.copy(login = login, loginError = validateLogin(login)) }
        validateLoginForm()
    }

    fun updatePasswordField(password: String) {
        _loginUiState.update { it.copy(password = password, passwordError = validatePassword(password)) }
        validateLoginForm()
    }

    fun togglePasswordVisibility() {
        _loginUiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    private fun validateLoginForm() {
        val currentState = _loginUiState.value
        val isFormValid = currentState.loginError == null &&
                currentState.passwordError == null &&
                currentState.login.isNotBlank() &&
                currentState.password.isNotBlank()

        _loginUiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun login(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(_loginUiState.value.isFormValid)
        }
    }

    fun resetState() {
        _loginUiState.value = LoginUiState()
    }


    private fun validateLogin(login: String) = when {
        login.length < 3 -> "Login must be at least 3 characters" else -> null
    }

    private fun validatePassword(password: String) = when {
        password.length < 6 ->  "Password must be at least 6 characters" else -> null
    }
}