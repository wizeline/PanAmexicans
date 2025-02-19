package com.wizeline.panamexicans.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wizeline.panamexicans.presentation.main.map.MapRoot
import com.wizeline.panamexicans.presentation.main.sessions.MapSessionsRoot

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = MainNavRoute.Map.toString(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(MainNavRoute.Map.toString()) {
            MapRoot(viewModel = hiltViewModel())
        }
        composable(MainNavRoute.RouteGenerator.toString()) {
            Text(text = "Route Generator")
        }
        composable(MainNavRoute.Sessions.toString()) {
            MapSessionsRoot(viewModel = hiltViewModel())
        }
    }
}
