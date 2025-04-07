package com.example.stajh2test.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyInputField
import com.example.stajh2test.ui.components.MyPasswordField
import com.example.stajh2test.ui.components.ScreenContainer
import com.example.stajh2test.ui.components.ScreenHeaderText
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
    val loginState by viewModel.loginModel.loginUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenHeaderText(
                title = "Welcome Back",
                subtitle = "Sign in to continue"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Показуємо помилки Firebase
            loginState.firebaseError?.let { error ->
                Text (
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            MyInputField(
                value = loginState.email,
                onValueChange = { viewModel.loginModel.updateLoginField(it) },
                label = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = loginState.emailError != null,
                errorMessage = loginState.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            MyPasswordField(
                value = loginState.password,
                onValueChange = { viewModel.loginModel.updatePasswordField(it) },
                label = "Password",
                isPasswordVisible = loginState.passwordVisible,
                onTogglePasswordVisibility = { viewModel.loginModel.togglePasswordVisibility() },
                isError = loginState.passwordError != null,
                errorMessage = loginState.passwordError
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextLink(
                text = "Forgot Password?",
                onClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (loginState.isLoading) {
                CircularProgressIndicator()
            } else {
                MyButton(
                    text = "Sign In",
                    onClick = {
                        viewModel.loginModel.clearErrors()
                        viewModel.loginModel.setLoading(true)
                        viewModel.loginUser(
                            email = loginState.email,
                            password = loginState.password,
                            onSuccess = {
                                viewModel.loginModel.setLoading(false)
                                onLoginSuccess()
                            },
                            onError = { error ->
                                viewModel.loginModel.setLoading(false)
                                viewModel.loginModel.setFirebaseError(error)
                            }
                        )
                    },
                    enabled = loginState.isFormValid && !loginState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextLink(
                text = "Don't have an account? Register",
                onClick = onRegisterClick
            )
        }
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