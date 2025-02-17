package com.wizeline.panamexicans.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wizeline.panamexicans.navigation.HomeNavRoute
import com.wizeline.panamexicans.navigation.AppNavRoute
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton

@Composable
fun HomeContentRoot(
    mainNavController: NavController,
    homeNavController: NavController,
    viewModel: HomeContentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.uiAction) {
        viewModel.uiAction.collect { event ->
            when (event) {
                is HomeContentUiAction.OnAccountLogged -> {
                    mainNavController.navigate(AppNavRoute.LOGIN.toString()) {
                        popUpTo(AppNavRoute.HOME.toString()) { inclusive = true }
                    }
                }
            }
        }
    }

    HomeContentScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                is HomeContentUiEvents.OnVoiceRecognitionClicked -> {
                    homeNavController.navigate(HomeNavRoute.VoiceRecognition.name)
                }

                is HomeContentUiEvents.OnIconUpdaterClicked -> {
                    homeNavController.navigate(HomeNavRoute.IconUpdater.name)
                }

                is HomeContentUiEvents.OnWidgetClicked -> {
                    homeNavController.navigate(HomeNavRoute.Widget.name)
                }

                is HomeContentUiEvents.OnCrashDetectorClicked -> {
                    homeNavController.navigate(HomeNavRoute.CrashDetector.name)
                }

                is HomeContentUiEvents.OnRouteCalculatorClicked -> {
                    homeNavController.navigate(HomeNavRoute.RouteCalculator.name)
                }

                is HomeContentUiEvents.OnRideSessionsClicked -> {
                    homeNavController.navigate(HomeNavRoute.RideSessions.name)
                }

                is HomeContentUiEvents.OnFunctionalPocClicked -> {
                    mainNavController.navigate(AppNavRoute.MAIN.name)
                }

                else -> Unit
            }
        }
    )
}

@Composable
fun HomeContentScreen(
    onEvent: (HomeContentUiEvents) -> Unit,
    uiState: HomeContentUiState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Voice Recognition",
                onClick = { onEvent(HomeContentUiEvents.OnVoiceRecognitionClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Icon Updater",
                onClick = { onEvent(HomeContentUiEvents.OnIconUpdaterClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Widget - Gamification",
                onClick = { onEvent(HomeContentUiEvents.OnWidgetClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Crash Detector",
                onClick = { onEvent(HomeContentUiEvents.OnCrashDetectorClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Route Calculator",
                onClick = { onEvent(HomeContentUiEvents.OnRouteCalculatorClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Ride Sessions",
                onClick = { onEvent(HomeContentUiEvents.OnRideSessionsClicked) })

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Functional POC",
                onClick = { onEvent(HomeContentUiEvents.OnFunctionalPocClicked) })

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .padding(vertical = 16.dp)
            )

            PrimaryColorButton(
                modifier = Modifier.fillMaxWidth(.7f),
                text = "Logout",
                onClick = { onEvent(HomeContentUiEvents.OnLogoutClicked) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentScreenPreview() {
    HomeContentScreen(onEvent = {}, uiState = HomeContentUiState())
}
