package com.example.stajh2test.ui.screens


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.stajh2test.ui.states.RegisterUiState
import com.example.stajh2test.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val registerState by viewModel.registerModel.registerUiState.collectAsState()

    ScreenContainer (modifier = Modifier.padding(horizontal = 16.dp)) {
        ScreenHeaderText(
            title = "Create Account",
            subtitle = "Start by Creating an Account"
        )

        MyInputField(
            value = registerState.code,
            onValueChange = { viewModel.registerModel.updateCodeField(it) },
            label = "Application Code",
            isError = registerState.codeError != null,
            errorMessage = registerState.codeError
        )

        MyInputField(
            value = registerState.login,
            onValueChange = { viewModel.registerModel.updateRegisterLogin(it) },
            label = "Login",
            isError = registerState.loginError != null,
            errorMessage = registerState.loginError
        )

        MyInputField(
            value = registerState.email,
            onValueChange = { viewModel.registerModel.updateEmailField(it) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = registerState.emailError != null,
            errorMessage = registerState.emailError
        )

        MyPasswordField(
            value = registerState.password,
            onValueChange = { viewModel.registerModel.updateRegisterPassword(it) },
            label = "Password",
            isPasswordVisible = registerState.passwordVisible,
            onTogglePasswordVisibility = { viewModel.registerModel.toggleRegisterPasswordVisibility() },
            isError = registerState.passwordError != null,
            errorMessage = registerState.passwordError
        )

        MyPasswordField(
            value = registerState.confirmPassword,
            onValueChange = { viewModel.registerModel.updateConfirmPassword(it) },
            label = "Confirm Password",
            isPasswordVisible = registerState.confirmPasswordVisible,
            onTogglePasswordVisibility = { viewModel.registerModel.toggleConfirmPasswordVisibility() },
            isError = registerState.confirmPasswordError != null,
            errorMessage = registerState.confirmPasswordError
        )

        MyButton(
            text = "Sign Up",
            onClick = { viewModel.registerModel.register { if(it) onRegisterSuccess() } },
            enabled = registerState.isFormValid
        )

        TextLink(
            text = "Already have an account? Login",
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