package com.example.stajh2test.ui.screens


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.ui.components.AuthScreenContainer
import com.example.stajh2test.ui.components.AuthScreenHeader
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyInputField
import com.example.stajh2test.ui.components.MyPasswordField
import com.example.stajh2test.ui.components.TextLink
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val loginState by viewModel.loginModel.loginUiState.collectAsState()

    AuthScreenContainer {
        AuthScreenHeader(
            title = "Welcome Back",
            subtitle = "Sign in to continue"
        )

        MyInputField(
            value = loginState.login,
            onValueChange = { viewModel.loginModel.updateLoginField(it) },
            label = "Login",
            isError = loginState.loginError != null,
            errorMessage = loginState.loginError
        )

        MyPasswordField(
            value = loginState.password,
            onValueChange = { viewModel.loginModel.updatePasswordField(it) },
            label = "Password",
            isPasswordVisible = loginState.passwordVisible,
            onTogglePasswordVisibility = { viewModel.loginModel.togglePasswordVisibility() },
            isError = loginState.passwordError != null,
            errorMessage = loginState.passwordError
        )

        TextLink(
            text = "Forgot Password?",
            onClick = onForgotPasswordClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        MyButton(
            text = "Sign In",
            onClick = { viewModel.loginModel.login { if(it) onLoginSuccess() } },
            enabled = loginState.isFormValid
        )

        TextLink(
            text = "Don't have an account? Register",
            onClick = onRegisterClick
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PrevLoginScreen() {
    Stajh2TestTheme {
        LoginScreen(
            viewModel = AuthViewModel(),
            onLoginSuccess = {},
            onRegisterClick = {},
            onForgotPasswordClick = {}
        )
    }
}