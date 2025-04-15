package com.example.stajh2test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stajh2test.navigation.AppNavHost
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.Model.authModel.AuthViewModel
import com.example.stajh2test.ui.states.FirebaseConfigCheck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val authViewModel = AuthViewModel()

        setContent {
            Stajh2TestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(authViewModel = authViewModel)
                }
            }
        }
    }
}