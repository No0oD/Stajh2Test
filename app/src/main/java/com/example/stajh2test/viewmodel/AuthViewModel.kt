package com.example.stajh2test.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.stajh2test.viewmodel.Model.ForgotPasswordModel
import com.example.stajh2test.viewmodel.Model.LoginModel
import com.example.stajh2test.viewmodel.Model.NewPasswordModel
import com.example.stajh2test.viewmodel.Model.VerificationModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    val registerModel = RegisterModel()
    val loginModel = LoginModel()
    val forgotPasswordModel = ForgotPasswordModel()
    val verificationModel = VerificationModel()
    val newPasswordModel = NewPasswordModel()

    private val auth: FirebaseAuth = Firebase.auth


    fun registerUser(
        email: String,
        password: String,
        additionalData: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Зберігаємо додаткові дані в Firestore (якщо потрібно)
            authResult.user?.uid?.let { uid ->
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(additionalData)
            }

            onSuccess()
        } catch (e: Exception) {
            onError(e.parseRegistrationError())
        }
    }

    private fun Exception.parseRegistrationError(): String = when (this) {
        is FirebaseAuthWeakPasswordException -> "Пароль повинен містити мінімум 6 символів"
        is FirebaseAuthUserCollisionException -> "Користувач з таким email вже існує"
        else -> "Помилка реєстрації: ${localizedMessage}"
    }


    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .await()
            onSuccess()
        } catch (e: Exception) {
            onError(e.parseFirebaseError())
        }
    }

    private fun Exception.parseFirebaseError(): String = when (this) {
        is FirebaseAuthInvalidUserException -> "Користувача не знайдено"
        is FirebaseAuthInvalidCredentialsException -> "Невірний пароль або email"
        else -> "Помилка авторизації: ${localizedMessage}"
    }
}
