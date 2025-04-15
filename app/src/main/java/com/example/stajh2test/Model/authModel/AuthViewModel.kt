package com.example.stajh2test.Model.authModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stajh2test.Model.resetpassModel.ForgotPasswordModel
import com.example.stajh2test.Model.resetpassModel.NewPasswordModel
import com.example.stajh2test.Model.resetpassModel.VerificationModel
import com.example.stajh2test.db.User
import com.example.stajh2test.repository.FirebaseRepository
import kotlinx.coroutines.launch
import android.util.Log

/**
 * Main AuthViewModel that coordinates authentication-related operations
 * and contains references to all the specialized view models.
 */
class AuthViewModel : ViewModel() {
    // Component ViewModels
    val registerModel = RegisterModel()
    val loginModel = LoginModel()
    val forgotPasswordModel = ForgotPasswordModel()
    val verificationModel = VerificationModel()
    val newPasswordModel = NewPasswordModel()

    // Shared repository for Firebase operations
    private val repository = FirebaseRepository()

    // Temporary storage for verification process
    private var currentEmail: String? = null

    private val TAG = "AuthViewModel"

    init {
        // Connect UI models to Firebase operations
        setupModelConnections()
        Log.d(TAG, "AuthViewModel initialized and model connections set up")
    }

    private fun setupModelConnections() {
        // Connect ForgotPasswordModel to Firebase operations
        forgotPasswordModel.onSendVerificationCode = { email, onSuccess, onError ->
            currentEmail = email
            Log.d(TAG, "Sending verification code to: $email")

            viewModelScope.launch {
                try {
                    val result = repository.sendVerificationCode(email)
                    result.fold(
                        onSuccess = {
                            Log.d(TAG, "Verification code sent successfully")
                            onSuccess()
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Error sending verification code: ${error.message}")
                            onError(error.message ?: "Failed to send verification code")
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Exception when sending verification code", e)
                    onError("Error: ${e.message}")
                }
            }
        }

        // Connect VerificationModel to Firebase operations
        verificationModel.onVerifyCode = verificationLabel@{ code, onSuccess, onError ->
            val email = currentEmail
            if (email == null) {
                Log.e(TAG, "Email not found for verification")
                onError("Session expired. Please try again.")
                return@verificationLabel // Use the defined label
            }

            Log.d(TAG, "Verifying code for email: $email")
            viewModelScope.launch {
                try {
                    val result = repository.verifyCode(email, code)
                    result.fold(
                        onSuccess = {
                            Log.d(TAG, "Code verified successfully")
                            onSuccess()
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Error verifying code: ${error.message}")
                            onError(error.message ?: "Failed to verify code")
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Exception when verifying code", e)
                    onError("Error: ${e.message}")
                }
            }
        }

        // Connect NewPasswordModel to Firebase operations
        newPasswordModel.onSubmitNewPassword = passwordLabel@{ password, onSuccess, onError ->
            val email = currentEmail
            if (email == null) {
                Log.e(TAG, "Email not found for password reset")
                onError("Session expired. Please try again.")
                return@passwordLabel // Use the defined label
            }

            Log.d(TAG, "Resetting password for email: $email")
            viewModelScope.launch {
                try {
                    val result = repository.resetPassword(email, password)
                    result.fold(
                        onSuccess = {
                            Log.d(TAG, "Password reset successfully")
                            onSuccess()
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Error resetting password: ${error.message}")
                            onError(error.message ?: "Failed to reset password")
                        }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Exception when resetting password", e)
                    onError("Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Register a new user with email and password
     */
    fun registerUser(
        email: String,
        password: String,
        additionalData: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        registerModel.setLoading(true)

        val code = additionalData["code"] as? String ?: ""
        val login = additionalData["login"] as? String ?: ""

        val user = User(
            code = code,
            login = login,
            email = email,
            password = password
        )

        try {
            val result = repository.registerUser(user)
            result.fold(
                onSuccess = {
                    registerModel.setLoading(false)
                    onSuccess()
                },
                onFailure = { error ->
                    registerModel.setLoading(false)
                    registerModel.setFirebaseError(error.message)
                    onError(error.message ?: "Помилка реєстрації")
                }
            )
        } catch (e: Exception) {
            registerModel.setLoading(false)
            registerModel.setFirebaseError(e.message)
            onError(e.message ?: "Помилка реєстрації")
        }
    }

    /**
     * Login a user with email and password
     */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        loginModel.setLoading(true)

        try {
            val result = repository.loginUser(email, password)
            result.fold(
                onSuccess = {
                    loginModel.setLoading(false)
                    onSuccess()
                },
                onFailure = { error ->
                    loginModel.setLoading(false)
                    loginModel.setFirebaseError(error.message)
                    onError(error.message ?: "Помилка авторизації")
                }
            )
        } catch (e: Exception) {
            loginModel.setLoading(false)
            loginModel.setFirebaseError(e.message)
            onError(e.message ?: "Помилка авторизації")
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        repository.signOut()
    }
}