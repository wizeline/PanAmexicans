package com.wizeline.panamexicans.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetectorRoot
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetectorViewModel
import com.wizeline.panamexicans.presentation.home.HomeContentRoot
import com.wizeline.panamexicans.presentation.home.HomeContentViewModel
import com.wizeline.panamexicans.presentation.iconupdater.IconUpdaterRoot
import com.wizeline.panamexicans.presentation.iconupdater.IconUpdaterViewModel
import com.wizeline.panamexicans.presentation.ridesessions.RideSessionsRoot
import com.wizeline.panamexicans.presentation.ridesessions.RideSessionsViewModel
import com.wizeline.panamexicans.presentation.routecalculator.RouteCalculatorRoot
import com.wizeline.panamexicans.presentation.routecalculator.RouteCalculatorViewModel
import com.wizeline.panamexicans.presentation.voicerecognition.VoiceRecognitionRoot
import com.wizeline.panamexicans.presentation.voicerecognition.VoiceRecognitionViewModel
import com.wizeline.panamexicans.presentation.widget.WidgetRoot
import com.wizeline.panamexicans.presentation.widget.WidgetViewModel

@Composable
fun HomeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainNavController: NavController,
    startDestination: String = HomeNavRoute.Home.name
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(HomeNavRoute.Home.name) {
            val viewModel: HomeContentViewModel = hiltViewModel()
            HomeContentRoot(mainNavController, navController, viewModel = viewModel)
        }
        composable(HomeNavRoute.VoiceRecognition.name) {
            val viewModel: VoiceRecognitionViewModel = hiltViewModel()
            VoiceRecognitionRoot(navController, viewModel)
        }
        composable(HomeNavRoute.IconUpdater.name) {
            val viewModel: IconUpdaterViewModel = hiltViewModel()
            IconUpdaterRoot(navController, viewModel)
        }
        composable(HomeNavRoute.Widget.name) {
            val viewModel: WidgetViewModel = hiltViewModel()
            WidgetRoot(navController, viewModel)
        }
        composable(HomeNavRoute.CrashDetector.name) {
            val viewModel: CrashDetectorViewModel = hiltViewModel()
            CrashDetectorRoot(navController, viewModel)
        }
        composable(HomeNavRoute.RouteCalculator.name) {
            val viewModel: RouteCalculatorViewModel = hiltViewModel()
            RouteCalculatorRoot(navController, viewModel)
        }
        composable(HomeNavRoute.RideSessions.name) {
            val viewModel: RideSessionsViewModel = hiltViewModel()
            RideSessionsRoot(navController, viewModel)
        }
    }
}
