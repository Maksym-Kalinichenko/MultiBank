package com.realtime.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.realtime.navigation.ScreenRoutes
import com.realtime.presentation.view.home.HomeScreen
import com.realtime.presentation.viewModel.mainScreen.MainScreenViewModel

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    mainScreenViewModel: MainScreenViewModel = hiltViewModel()
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = ScreenRoutes.HomeScreen.route,
                    modifier = Modifier
                ) {
                    composable(ScreenRoutes.HomeScreen.route) {
                        HomeScreen(modifier = Modifier)
                    }
                }
            }
        }
    }
}