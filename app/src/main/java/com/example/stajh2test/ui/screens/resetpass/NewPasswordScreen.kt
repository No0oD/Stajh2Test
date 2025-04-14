package com.example.stajh2test.ui.screens.resetpass

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.MyPasswordField
import com.example.stajh2test.ui.components.ScreenContainer
import com.example.stajh2test.ui.components.ScreenHeaderText
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.Model.authModel.AuthViewModel

@Composable
fun NewPasswordScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onPasswordUpdateSuccess: () -> Unit
) {
    val newPasswordState by viewModel.newPasswordModel.uiState.collectAsState()

    ScreenContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        ScreenHeaderText(
            title = "Create a New Password",
            subtitle = "Enter your new password."
        )

        MyPasswordField(
            value = newPasswordState.password,
            onValueChange = { viewModel.newPasswordModel.updatePassword(it) },
            label = "New Password",
            isPasswordVisible = newPasswordState.passwordVisible,
            onTogglePasswordVisibility = { viewModel.newPasswordModel.togglePasswordVisibility() },
            isError = newPasswordState.passwordError != null,
            errorMessage = newPasswordState.passwordError
        )

        MyPasswordField(
            value = newPasswordState.confirmPassword,
            onValueChange = { viewModel.newPasswordModel.updateConfirmPassword(it) },
            label = "Confirm Password",
            isPasswordVisible = newPasswordState.confirmPasswordVisible,
            onTogglePasswordVisibility = { viewModel.newPasswordModel.toggleConfirmPasswordVisibility() },
            isError = newPasswordState.confirmPasswordError != null,
            errorMessage = newPasswordState.confirmPasswordError
        )

        if (newPasswordState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            MyButton(
                text = "Update Password",
                onClick = {
                    viewModel.newPasswordModel.submitNewPassword(onPasswordUpdateSuccess)
                },
                enabled = newPasswordState.isFormValid
            )

            MyButton(
                text = "Back",
                onClick = onBackClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrevNewPasswordScreen() {
    Stajh2TestTheme {
        NewPasswordScreen(
            viewModel = AuthViewModel(),
            onBackClick = {},
            onPasswordUpdateSuccess = {}
        )
    }
}
