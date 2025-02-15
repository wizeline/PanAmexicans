package com.wizeline.panamexicans.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.presentation.login.LoginComposable
import com.wizeline.panamexicans.presentation.login.LoginUiEvents
import com.wizeline.panamexicans.presentation.login.LoginUiState
import com.wizeline.panamexicans.presentation.login.RegisterRoot

sealed class LoginRoute(val route: String, @StringRes val title: Int) {
    data object Login : LoginRoute("login", R.string.login)
    data object Register : LoginRoute("register", R.string.register)
}

@Composable
fun LoginNavHost(
    modifier: Modifier = Modifier,
    mainNavController: NavHostController,
    loginNavController: NavHostController,
    startDestination: String = "login",
    uiState: LoginUiState,
    onEvent: (LoginUiEvents) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(
        navController = loginNavController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(LoginRoute.Login.route) {
            LoginComposable(
                uiState = uiState,
                onEvent = onEvent,
            )
        }
        composable(LoginRoute.Register.route) {
            RegisterRoot(mainNavController = mainNavController, loginNavController = loginNavController, viewModel = hiltViewModel())
        }
    }

}