package com.example.stajh2test.ui.states

import android.util.Patterns

/**
 * Utility class for validating user input.
 * Centralizes validation logic to avoid duplication.
 */
object ValidationUtils {

    fun validateEmail(email: String): String? = when {
        email.isEmpty() -> "Email не може бути порожнім"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Невірний формат email"
        else -> null
    }

    fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Пароль не може бути порожнім"
        password.length < 6 -> "Пароль повинен містити мінімум 6 символів"
        else -> null
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? = when {
        confirmPassword != password -> "Паролі не співпадають"
        else -> null
    }

    fun validateCode(code: String): String? = when {
        code.isEmpty() -> "Код програми не може бути порожнім"
        code.length < 3 -> "Код повинен містити мінімум 3 символи"
        else -> null
    }

    fun validateLogin(login: String): String? = when {
        login.isEmpty() -> "Логін не може бути порожнім"
        login.length < 3 -> "Логін повинен містити мінімум 3 символи"
        else -> null
    }
}