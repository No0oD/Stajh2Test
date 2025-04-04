package com.example.stajh2test.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.R
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
    val loginState by viewModel.loginUiState.collectAsState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(164.dp))

            // Title
            Text(
                text = "My App",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Login Field
            MyInputField(
                value = loginState.login,
                onValueChange = { viewModel.updateLoginField(it) },
                label = "Login",
                isError = loginState.loginError != null,
                errorMessage = loginState.loginError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // Password Field
            MyPasswordField(
                value = loginState.password,
                onValueChange = { viewModel.updatePasswordField(it) },
                label = "Password",
                isPasswordVisible = loginState.passwordVisible,
                onTogglePasswordVisibility = { viewModel.togglePasswordVisibility() },
                isError = loginState.passwordError != null,
                errorMessage = loginState.passwordError
            )
            
            // Forgot Password Link
            TextLink(
                text = "Forgot Password?",
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Login Button
            MyButton(
                text = "Login",
                onClick = { 
                    if (viewModel.login()) {
                        onLoginSuccess() 
                    }
                },
                enabled = loginState.isFormValid
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Register Button
            MyButton(
                text = "Register",
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
