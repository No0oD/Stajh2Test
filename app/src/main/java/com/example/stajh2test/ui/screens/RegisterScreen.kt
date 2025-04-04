package com.example.stajh2test.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyInputField
import com.example.stajh2test.ui.components.MyPasswordField
import com.example.stajh2test.ui.components.TextLink
import com.example.stajh2test.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val registerState by viewModel.registerUiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = "Start by Creating an Account",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Application Code
            MyInputField(
                value = registerState.code,
                onValueChange = { viewModel.updateCodeField(it) },
                label = "Application Code",
                isError = registerState.codeError != null,
                errorMessage = registerState.codeError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // Login
            MyInputField(
                value = registerState.login,
                onValueChange = { viewModel.updateRegisterLogin(it) },
                label = "Login",
                isError = registerState.loginError != null,
                errorMessage = registerState.loginError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // Email
            MyInputField(
                value = registerState.email,
                onValueChange = { viewModel.updateEmailField(it) },
                label = "Email",
                isError = registerState.emailError != null,
                errorMessage = registerState.emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Password
            MyPasswordField(
                value = registerState.password,
                onValueChange = { viewModel.updateRegisterPassword(it) },
                label = "Password",
                isPasswordVisible = registerState.passwordVisible,
                onTogglePasswordVisibility = { viewModel.toggleRegisterPasswordVisibility() },
                isError = registerState.passwordError != null,
                errorMessage = registerState.passwordError
            )
            
            // Confirm Password
            MyPasswordField(
                value = registerState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = "Confirm Password",
                isPasswordVisible = registerState.confirmPasswordVisible,
                onTogglePasswordVisibility = { viewModel.toggleConfirmPasswordVisibility() },
                isError = registerState.confirmPasswordError != null,
                errorMessage = registerState.confirmPasswordError
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign Up Button
            MyButton(
                text = "Sign Up",
                onClick = { 
                    if (viewModel.register()) {
                        onRegisterSuccess() 
                    }
                },
                enabled = registerState.isFormValid
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Already have an account? Login link
            TextLink(
                text = "Already have an account? Login",
                onClick = onLoginClick
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
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
