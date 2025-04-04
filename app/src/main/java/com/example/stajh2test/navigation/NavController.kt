package com.example.stajh2test.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stajh2test.ui.screens.ForgotPasswordScreen
import com.example.stajh2test.ui.screens.HomeScreen
import com.example.stajh2test.ui.screens.LoginScreen
import com.example.stajh2test.ui.screens.RegisterScreen
import com.example.stajh2test.viewmodel.AuthViewModel

// Navigation routes
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = remember { AuthViewModel() },
    startDestination: String = Screen.Register.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // Clear the back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        // Clear the back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                        }
                        // Avoid multiple copies
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    // Explicitly navigate to Login screen instead of popping back stack
                    navController.navigate(Screen.Login.route) {
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        // Clear the back stack
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
