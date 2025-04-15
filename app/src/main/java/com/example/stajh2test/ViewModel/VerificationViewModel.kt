package com.example.stajh2test.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.repository.FirebaseRepository
import com.example.stajh2test.ui.states.VerificationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerificationViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerificationUiState())
    val uiState: StateFlow<VerificationUiState> = _uiState.asStateFlow()

    // Temporary storage for the email being verified
    private var emailToVerify: String = ""

    fun setEmailToVerify(email: String) {
        emailToVerify = email
    }

    fun updateDigit1(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit1 = digit) }
            validateForm()
        }
    }

    fun updateDigit2(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit2 = digit) }
            validateForm()
        }
    }

    fun updateDigit3(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit3 = digit) }
            validateForm()
        }
    }

    fun updateDigit4(digit: String) {
        if (digit.length <= 1 && (digit.isEmpty() || digit.all { it.isDigit() })) {
            _uiState.update { it.copy(digit4 = digit) }
            validateForm()
        }
    }

    private fun validateForm() {
        val currentState = _uiState.value
        val isFormValid = currentState.digit1.isNotEmpty() &&
                currentState.digit2.isNotEmpty() &&
                currentState.digit3.isNotEmpty() &&
                currentState.digit4.isNotEmpty()

        _uiState.update { it.copy(isFormValid = isFormValid) }
    }

    fun verifyCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.isFormValid) {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val code = _uiState.value.run {
                    digit1 + digit2 + digit3 + digit4
                }

                val result = repository.verifyCode(emailToVerify, code)

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

    fun resendVerificationCode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.sendVerificationCode(emailToVerify)

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

    fun clearErrors() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetState() {
        _uiState.value = VerificationUiState()
    }
}