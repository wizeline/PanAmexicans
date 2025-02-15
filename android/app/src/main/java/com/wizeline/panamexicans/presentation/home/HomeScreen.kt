package com.wizeline.panamexicans.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.navigation.HomeNavHost
import com.wizeline.panamexicans.navigation.HomeNavRoute
import com.wizeline.panamexicans.navigation.LoginRoute

@Composable
fun HomeRoot(navController: NavController, viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val homeNavController = rememberNavController()

    HomeScreen(
        mainNavController = navController,
        homeNavController = homeNavController,
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                is HomeUiEvents.OnBackPressed -> {
                    homeNavController.popBackStack()
                }
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEvent: (HomeUiEvents) -> Unit,
    uiState: HomeUiState,
    mainNavController: NavController,
    homeNavController: NavHostController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by homeNavController.currentBackStackEntryAsState()

    val title = when (currentBackStackEntry?.destination?.route) {
        HomeNavRoute.Home.name -> stringResource(R.string.home)
        HomeNavRoute.VoiceRecognition.name -> stringResource(R.string.voice_recognition)
        HomeNavRoute.IconUpdater.name -> stringResource(R.string.icon_updater)
        HomeNavRoute.Widget.name -> stringResource(R.string.widget)
        HomeNavRoute.CrashDetector.name -> stringResource(R.string.crash_detector)
        HomeNavRoute.RouteCalculator.name -> stringResource(R.string.route_calculator)
        HomeNavRoute.RideSessions.name -> stringResource(R.string.ridesessions)
        else -> null
    }

    Scaffold(
        Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                title = {
                    title?.let {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = it.uppercase()
                        )
                    }
                },
                navigationIcon = {
                    if (currentBackStackEntry?.destination?.route != HomeNavRoute.Home.name) {
                        IconButton(
                            onClick = { onEvent(HomeUiEvents.OnBackPressed)},
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(36.dp))
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        HomeNavHost(
            mainNavController = mainNavController,
            navController = homeNavController,
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp),
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        onEvent = {},
        uiState = HomeUiState(),
        rememberNavController(),
        rememberNavController()
    )
}
