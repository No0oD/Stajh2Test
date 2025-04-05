package com.example.stajh2test.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyInputField
import com.example.stajh2test.ui.components.ScreenContainer
import com.example.stajh2test.ui.components.ScreenHeaderText
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onVerificationCodeSent: () -> Unit
) {
    val forgotPasswordState by viewModel.forgotPasswordModel.uiState.collectAsState()

    ScreenContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        ScreenHeaderText(
            title = "Password Recovery",
            subtitle = "Enter your email address to receive a verification code."
        )

        MyInputField(
            value = forgotPasswordState.email,
            onValueChange = { viewModel.forgotPasswordModel.updateEmailField(it) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = forgotPasswordState.emailError != null,
            errorMessage = forgotPasswordState.emailError
        )

        if (forgotPasswordState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            MyButton(
                text = "Send",
                onClick = {
                    viewModel.forgotPasswordModel.sendVerificationCode(onVerificationCodeSent)
                },
                enabled = forgotPasswordState.isFormValid
            )

            MyButton(
                text = "Back to Login",
                onClick = onBackClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevForgotPasswordScreen() {
    Stajh2TestTheme {
        ForgotPasswordScreen(
            viewModel = AuthViewModel(),
            onBackClick = {},
            onVerificationCodeSent = {}
        )
    }
}
