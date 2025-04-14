package com.example.stajh2test.ui.screens.auth


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.ui.components.ScreenContainer
import com.example.stajh2test.ui.components.ScreenHeaderText
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyInputField
import com.example.stajh2test.ui.components.MyPasswordField
import com.example.stajh2test.ui.components.TextLink
import com.example.stajh2test.Model.authModel.AuthViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val registerState by viewModel.registerModel.registerUiState.collectAsStateWithLifecycle()

    ScreenContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        ScreenHeaderText(
            title = "Створення акаунту",
            subtitle = "Почніть з реєстрації"
        )

        // Поле коду програми
        MyInputField(
            value = registerState.code,
            onValueChange = { viewModel.registerModel.updateCodeField(it) },
            label = "Код програми",
            isError = registerState.codeError != null,
            errorMessage = registerState.codeError
        )

        // Поле логіну
        MyInputField(
            value = registerState.login,
            onValueChange = { viewModel.registerModel.updateRegisterLogin(it) },
            label = "Логін",
            isError = registerState.loginError != null,
            errorMessage = registerState.loginError
        )

        // Поле email
        MyInputField(
            value = registerState.email,
            onValueChange = { viewModel.registerModel.updateEmailField(it) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = registerState.emailError != null,
            errorMessage = registerState.emailError
        )

        // Поле паролю
        MyPasswordField(
            value = registerState.password,
            onValueChange = { viewModel.registerModel.updateRegisterPassword(it) },
            label = "Пароль",
            isPasswordVisible = registerState.passwordVisible,
            onTogglePasswordVisibility = { viewModel.registerModel.toggleRegisterPasswordVisibility() },
            isError = registerState.passwordError != null,
            errorMessage = registerState.passwordError
        )

        // Підтвердження паролю
        MyPasswordField(
            value = registerState.confirmPassword,
            onValueChange = { viewModel.registerModel.updateConfirmPassword(it) },
            label = "Підтвердження паролю",
            isPasswordVisible = registerState.confirmPasswordVisible,
            onTogglePasswordVisibility = { viewModel.registerModel.toggleConfirmPasswordVisibility() },
            isError = registerState.confirmPasswordError != null,
            errorMessage = registerState.confirmPasswordError
        )

        // Помилка Firebase
        registerState.firebaseError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка реєстрації або індикатор завантаження
        if (registerState.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator()
            }
        } else {
            MyButton(
                text = "Зареєструватися",
                onClick = {
                    viewModel.registerModel.clearErrors()
                    if (registerState.isFormValid) {
                        viewModel.registerModel.setLoading(true)

                        val additionalData = mapOf(
                            "code" to registerState.code,
                            "login" to registerState.login,
                            "createdAt" to System.currentTimeMillis()
                        )

                        viewModel.registerUser(
                            email = registerState.email,
                            password = registerState.password,
                            additionalData = additionalData,
                            onSuccess = {
                                viewModel.registerModel.setLoading(false)
                                onRegisterSuccess()
                            },
                            onError = { error ->
                                viewModel.registerModel.setLoading(false)
                                viewModel.registerModel.setFirebaseError(error)
                            }
                        )
                    }
                },
                enabled = registerState.isFormValid && !registerState.isLoading
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Посилання на сторінку входу
        TextLink(
            text = "Вже маєте акаунт? Увійти",
            onClick = onLoginClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrevRegisterScreen() {
    Stajh2TestTheme {
        RegisterScreen(
            viewModel = AuthViewModel(),
            onRegisterSuccess = {},
            onLoginClick = {}
        )
    }
}