package com.wizeline.panamexicans.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wizeline.panamexicans.presentation.home.HomeRoot
import com.wizeline.panamexicans.presentation.login.LoginRoot
import com.wizeline.panamexicans.presentation.main.MainRoot
import com.wizeline.panamexicans.presentation.main.MainViewModel
import com.wizeline.panamexicans.presentation.splash.SplashScreenRoot
import com.wizeline.panamexicans.presentation.splash.SplashScreenViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = AppNavRoute.SPLASH.toString(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(AppNavRoute.SPLASH.toString()) {
            val viewModel: SplashScreenViewModel = hiltViewModel()
            SplashScreenRoot(navController = navController, viewModel = viewModel)
        }
        composable(AppNavRoute.HOME.toString()) {
            HomeRoot(navController =  navController, hiltViewModel())
        }
        composable(AppNavRoute.LOGIN.toString()) {
            LoginRoot(navController, hiltViewModel())
        }
        composable(AppNavRoute.MAIN.name) {
            val viewModel: MainViewModel = hiltViewModel()
            MainRoot(navController, viewModel)
        }
    }
}
