package com.realtime.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.realtime.presentation.MainScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenRoutes.FinalDestination.route) {
        composable(ScreenRoutes.FinalDestination.route) {
            MainScreen(mainNavController = navController)
        }
    }
}

sealed class ScreenRoutes(val route: String) {
    data object FinalDestination : ScreenRoutes("final_destination")
    data object HomeScreen : ScreenRoutes("home_screen")
}