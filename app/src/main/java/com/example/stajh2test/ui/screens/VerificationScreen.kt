package com.example.stajh2test.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stajh2test.ui.components.MyButton
import com.example.stajh2test.ui.components.ScreenContainer
import com.example.stajh2test.ui.components.ScreenHeaderText
import com.example.stajh2test.ui.components.SingleDigitField
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.viewmodel.AuthViewModel

@Composable
fun VerificationScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit
) {
    val verificationState by viewModel.verificationModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    
    // Setup focus requesters for each digit field
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }
    
    // Focus the first field when the screen is shown
    LaunchedEffect(Unit) {
        focusRequester1.requestFocus()
    }

    ScreenContainer(modifier = Modifier.padding(horizontal = 16.dp)) {
        ScreenHeaderText(
            title = "Verification",
            subtitle = "Enter the verification code sent to your email."
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First digit field
            SingleDigitField(
                value = verificationState.digit1,
                onValueChange = { viewModel.verificationModel.updateDigit1(it) },
                modifier = Modifier.width(64.dp),
                focusRequester = focusRequester1,
                onNext = { focusRequester2.requestFocus() }
            )
            
            // Second digit field
            SingleDigitField(
                value = verificationState.digit2,
                onValueChange = { viewModel.verificationModel.updateDigit2(it) },
                modifier = Modifier.width(64.dp),
                focusRequester = focusRequester2,
                onNext = { focusRequester3.requestFocus() }
            )
            
            // Third digit field
            SingleDigitField(
                value = verificationState.digit3,
                onValueChange = { viewModel.verificationModel.updateDigit3(it) },
                modifier = Modifier.width(64.dp),
                focusRequester = focusRequester3,
                onNext = { focusRequester4.requestFocus() }
            )
            
            // Fourth digit field
            SingleDigitField(
                value = verificationState.digit4,
                onValueChange = { viewModel.verificationModel.updateDigit4(it) },
                modifier = Modifier.width(64.dp),
                focusRequester = focusRequester4,
                // Clear focus after last digit
                onNext = { focusManager.clearFocus() }
            )
        }

        if (verificationState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            MyButton(
                text = "Confirm",
                onClick = {
                    viewModel.verificationModel.verifyCode(onVerificationSuccess)
                },
                enabled = verificationState.isFormValid
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
fun PrevVerificationScreen() {
    Stajh2TestTheme {
        VerificationScreen(
            viewModel = AuthViewModel(),
            onBackClick = {},
            onVerificationSuccess = {}
        )
    }
}
