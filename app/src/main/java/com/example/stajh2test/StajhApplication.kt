package com.example.stajh2test

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.stajh2test.ui.states.FirebaseConfigCheck
import com.example.stajh2test.ui.states.TimberConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class StajhApplication : Application() {
    private val TAG = "StajhApplication"
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        TimberConfig.init(this)
        
        // Initialize Firebase
        initializeFirebase()
    }
    
    private fun initializeFirebase() {
        try {
            // Check if Firebase is already initialized to avoid multiple initializations
            if (FirebaseApp.getApps(this).isEmpty()) {
                // Add a small delay before initializing Firebase
                // This can help with timing issues on some devices
                Thread.sleep(100)
                
                // Initialize Firebase
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "Firebase initialized successfully")
            } else {
                Log.d(TAG, "Firebase was already initialized")
            }
            
            // Verify Firebase configuration
            FirebaseConfigCheck.verifyConfiguration()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase: ${e.message}", e)
            
            // Log additional diagnostic information
            try {
                val googleServicesFile = assets.open("google-services.json")
                val size = googleServicesFile.available()
                Log.d(TAG, "google-services.json size: $size bytes")
                googleServicesFile.close()
            } catch (fileEx: Exception) {
                Log.e(TAG, "Error accessing google-services.json: ${fileEx.message}", fileEx)
            }
        }
    }
}
