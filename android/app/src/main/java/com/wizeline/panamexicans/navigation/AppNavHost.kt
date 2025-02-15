package com.wizeline.panamexicans.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wizeline.panamexicans.presentation.home.HomeRoot
import com.wizeline.panamexicans.presentation.login.LoginRoot
import com.wizeline.panamexicans.presentation.splash.SplashScreenRoot
import com.wizeline.panamexicans.presentation.splash.SplashScreenViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = MainNavRoute.SPLASH.toString(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(MainNavRoute.SPLASH.toString()) {
            val viewModel: SplashScreenViewModel = hiltViewModel()
            SplashScreenRoot(navController = navController, viewModel = viewModel)
        }
        composable(MainNavRoute.HOME.toString()) {
            HomeRoot(navController =  navController, hiltViewModel())
        }
        composable(MainNavRoute.LOGIN.toString()) {
            LoginRoot(navController, hiltViewModel())
        }
    }
}
