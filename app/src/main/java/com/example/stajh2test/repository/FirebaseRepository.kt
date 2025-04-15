package com.example.stajh2test.repository

import android.util.Log
import com.example.stajh2test.db.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

/**
 * Repository for handling Firebase operations including authentication and verification.
 */
class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val functions: FirebaseFunctions = Firebase.functions

    private val TAG = "FirebaseRepository"

    /**
     * Register a new user with email and password
     */
    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            Log.d(TAG, "Registering user: ${user.email}")
            val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()

            // Save additional user data to Firestore
            authResult.user?.uid?.let { uid ->
                val userData = mapOf(
                    "code" to user.code,
                    "login" to user.login,
                    "email" to user.email,
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .await()
            }

            Log.d(TAG, "User registered successfully: ${user.email}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user: ${e.message}", e)
            Result.failure(parseRegistrationError(e))
        }
    }

    /**
     * Login a user with email and password
     */
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            Log.d(TAG, "Login attempt for user: $email")
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Login successful for user: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed for user $email: ${e.message}", e)
            Result.failure(parseLoginError(e))
        }
    }

    /**
     * Check if a user exists in Firebase Authentication
     */
    private suspend fun checkUserExists(email: String): Boolean {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email.trim()).await()
            val signInMethods = result.signInMethods ?: emptyList()
            Log.d(TAG, "Sign-in methods for $email: ${signInMethods.size}")

            // If there are any sign-in methods, the user exists
            val exists = signInMethods.isNotEmpty()

            if (!exists) {
                Log.d(TAG, "No sign-in methods found for $email")
            } else {
                Log.d(TAG, "User exists with sign-in methods: $signInMethods")
            }

            exists
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if user exists: ${e.message}", e)
            // Default to false on error to avoid security issues
            false
        }
    }
    /**
     * Send a verification code for password reset
     */
    /**
     * Send a verification code for password reset
     */
    suspend fun sendVerificationCode(email: String): Result<Unit> {
        val normalizedEmail = email.trim()

        return try {
            Log.d(TAG, "Sending verification code to: $normalizedEmail")

            // Check if user exists in Firebase Auth
            try {
                val result = auth.fetchSignInMethodsForEmail(normalizedEmail).await()
                val signInMethods = result.signInMethods ?: emptyList()

                Log.d(TAG, "Sign-in methods for $normalizedEmail: ${signInMethods.size}")

                if (signInMethods.isEmpty()) {
                    Log.d(TAG, "No user found with email: $normalizedEmail")
                    return Result.failure(Exception("Користувача з таким email не знайдено"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking email existence: ${e.message}", e)
                return Result.failure(Exception("Помилка перевірки email: ${e.localizedMessage}"))
            }

            // Generate a 4-digit code locally
            val verificationCode = (1000..9999).random().toString()

            // Store the code in Firestore with an expiration time (10 minutes)
            val expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10)

            val codeData = mapOf(
                "code" to verificationCode,
                "email" to normalizedEmail,
                "expirationTime" to expirationTime,
                "verified" to false,
                "createdAt" to System.currentTimeMillis()
            )

            // We'll use email as the document ID for easy retrieval
            firestore.collection("verificationCodes")
                .document(normalizedEmail)
                .set(codeData)
                .await()

            Log.d(TAG, "Verification code stored for: $normalizedEmail")

            // In a real app, we would send an email with the code here
            // For this example, we'll log the code for testing purposes
            Log.i(TAG, "TEST CODE for $normalizedEmail: $verificationCode")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending verification code: ${e.message}", e)
            Result.failure(Exception("Помилка надсилання коду: ${e.localizedMessage}"))
        }
    }

    /**
     * Verify the 4-digit code
     */
    suspend fun verifyCode(email: String, code: String): Result<Unit> {
        val normalizedEmail = email.trim()

        return try {
            Log.d(TAG, "Verifying code for: $normalizedEmail")
            val docRef = firestore.collection("verificationCodes").document(normalizedEmail)
            val document = docRef.get().await()

            if (!document.exists()) {
                Log.w(TAG, "No verification code found for: $normalizedEmail")
                return Result.failure(Exception("Код верифікації не знайдено"))
            }

            val storedCode = document.getString("code")
            val expirationTime = document.getLong("expirationTime") ?: 0

            when {
                System.currentTimeMillis() > expirationTime -> {
                    Log.w(TAG, "Verification code expired for: $normalizedEmail")
                    Result.failure(Exception("Код верифікації закінчився. Спробуйте ще раз"))
                }
                storedCode != code -> {
                    Log.w(TAG, "Invalid verification code for: $normalizedEmail")
                    Result.failure(Exception("Невірний код верифікації"))
                }
                else -> {
                    // Mark as verified
                    docRef.update("verified", true).await()
                    Log.d(TAG, "Code verified successfully for: $normalizedEmail")
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verifying code: ${e.message}", e)
            Result.failure(Exception("Помилка перевірки коду: ${e.localizedMessage}"))
        }
    }

    /**
     * Reset password for a verified email
     */
    suspend fun resetPassword(email: String, newPassword: String): Result<Unit> {
        val normalizedEmail = email.trim()

        return try {
            Log.d(TAG, "Attempting password reset for: $normalizedEmail")

            // Check if the email has been verified
            val docRef = firestore.collection("verificationCodes").document(normalizedEmail)
            val document = docRef.get().await()

            if (!document.exists() || document.getBoolean("verified") != true) {
                Log.w(TAG, "Email not verified for password reset: $normalizedEmail")
                return Result.failure(Exception("Email не верифіковано"))
            }

            // We need to reset the password directly instead of sending an email
            // First, we need to get a temporary authentication token
            try {
                // Use Firebase Admin SDK on backend or a custom auth token approach
                // But for now, we'll use a workaround by:

                // 1. Sign out current user
                auth.signOut()

                // 2. Send a password reset email
                // This allows resetting through Firebase's flow directly
                auth.sendPasswordResetEmail(normalizedEmail).await()
                Log.d(TAG, "Password reset email sent to: $normalizedEmail")

                // 3. Clean up the verification code
                docRef.delete().await()

                // 4. Note: The user will need to follow the email link to reset
                //    and then sign in again with the new password

                return Result.success(Unit)
            } catch (e: FirebaseAuthInvalidUserException) {
                Log.e(TAG, "User not found in Firebase Auth: $normalizedEmail", e)
                return Result.failure(Exception("Користувача з таким email не знайдено"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting password: ${e.message}", e)
            Result.failure(Exception("Помилка зміни пароля: ${e.localizedMessage}"))
        }
    }

    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Check if a user is currently signed in
     */
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Get the current user's email
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    /**
     * Parse registration errors into user-friendly messages
     */
    private fun parseRegistrationError(exception: Exception): Exception {
        val message = when (exception) {
            is FirebaseAuthWeakPasswordException -> "Пароль повинен містити мінімум 6 символів"
            is FirebaseAuthUserCollisionException -> "Користувач з таким email вже існує"
            else -> "Помилка реєстрації: ${exception.localizedMessage}"
        }
        return Exception(message)
    }

    /**
     * Parse login errors into user-friendly messages
     */
    private fun parseLoginError(exception: Exception): Exception {
        val message = when (exception) {
            is FirebaseAuthInvalidUserException -> "Користувача не знайдено"
            is FirebaseAuthInvalidCredentialsException -> "Невірний пароль або email"
            else -> "Помилка авторизації: ${exception.localizedMessage}"
        }
        return Exception(message)
    }
}