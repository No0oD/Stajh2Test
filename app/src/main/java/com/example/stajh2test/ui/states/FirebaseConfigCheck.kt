package com.example.stajh2test.ui.states

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

/**
 * Utility to check Firebase configuration
 */
object FirebaseConfigCheck {
    private const val TAG = "FirebaseConfigCheck"

    /**
     * Verify Firebase configuration
     * Call this from your Application class onCreate or splash screen
     */
    fun verifyConfiguration() {
        try {
            // Check if Firebase is initialized
            val apps = FirebaseApp.getApps(Firebase.app.applicationContext)
            Log.d(TAG, "Number of Firebase apps: ${apps.size}")
            
            val app = FirebaseApp.getInstance()
            Log.d(TAG, "Firebase App Name: ${app.name}")
            Log.d(TAG, "Firebase App Options: ${app.options}")
            Log.d(TAG, "Firebase Project ID: ${app.options.projectId}")
            Log.d(TAG, "Firebase Application ID: ${app.options.applicationId}")
            Log.d(TAG, "Firebase API Key: ${app.options.apiKey.take(5)}...")

            // Check Auth
            try {
                val auth = FirebaseAuth.getInstance()
                Log.d(TAG, "Firebase Auth Instance: ${auth.javaClass.simpleName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Firebase Auth: ${e.message}", e)
            }

            // Check Firestore
            try {
                val firestore = FirebaseFirestore.getInstance()
                Log.d(TAG, "Firebase Firestore Instance: ${firestore.javaClass.simpleName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Firebase Firestore: ${e.message}", e)
            }

            // Check Functions
            try {
                val functions = Firebase.functions
                Log.d(TAG, "Firebase Functions Instance: ${functions.javaClass.simpleName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Firebase Functions: ${e.message}", e)
            }

            Log.i(TAG, "Firebase configuration verification completed")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase configuration error: ${e.message}", e)
        }
    }
}