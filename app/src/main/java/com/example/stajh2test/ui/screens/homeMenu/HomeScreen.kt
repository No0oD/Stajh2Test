package com.example.stajh2test.ui.screens.homeMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stajh2test.ui.components.BottomNavBar
import com.example.stajh2test.ui.components.HomeItem
import com.example.stajh2test.ui.theme.Stajh2TestTheme
import com.example.stajh2test.ui.screens.features.HomeFun1Screen
import com.example.stajh2test.ui.screens.features.HomeFun2Screen
import com.example.stajh2test.ui.screens.features.HomeFun3Screen
import com.example.stajh2test.ui.screens.features.HomeFun4Screen

// Define routes for home features
sealed class HomeFeature(val route: String) {
    object Home : HomeFeature("home_main")
    object HomeFun1 : HomeFeature("home_fun1")
    object HomeFun2 : HomeFeature("home_fun2")
    object HomeFun3 : HomeFeature("home_fun3")
    object HomeFun4 : HomeFeature("home_fun4")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeFeature.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeFeature.Home.route) {
                HomeMainScreen(navController, onLogout)
            }
            composable(HomeFeature.HomeFun1.route) {
                HomeFun1Screen(navController)
            }
            composable(HomeFeature.HomeFun2.route) {
                HomeFun2Screen(navController)
            }
            composable(HomeFeature.HomeFun3.route) {
                HomeFun3Screen(navController)
            }
            composable(HomeFeature.HomeFun4.route) {
                HomeFun4Screen(navController)
            }
        }
    }
}

@Composable
fun HomeMainScreen(navController: NavController, onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Home Page",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            HomeItem(title = "Feature 1", subtitle = "Description for feature 1", onClick = { navController.navigate(HomeFeature.HomeFun1.route) })
            HomeItem(title = "Feature 2", subtitle = "Description for feature 2", onClick = { navController.navigate(HomeFeature.HomeFun2.route) })
            HomeItem(title = "Feature 3", subtitle = "Description for feature 3", onClick = { navController.navigate(HomeFeature.HomeFun3.route) })
            HomeItem(title = "Feature 4", subtitle = "Description for feature 4", onClick = { navController.navigate(HomeFeature.HomeFun4.route) })

            Spacer(modifier = Modifier.height(32.dp))

//            Button(
//                onClick = onLogout
//            ) {
//                Text("Logout")
//            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PrevHomeScreen() {
    Stajh2TestTheme {
        HomeScreen(onLogout = {})
    }
}
