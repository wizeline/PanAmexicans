package com.wizeline.panamexicans.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.navigation.MainNavRoute

@Composable
fun SplashScreenRoot(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SplashScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.uiAction) {
        viewModel.uiAction.collect { event ->
            when (event) {
                is UiAction.NavigateToLogin -> {
                    navController.navigate(MainNavRoute.LOGIN.toString()) {
                        popUpTo(MainNavRoute.SPLASH.toString()) { inclusive = true }
                    }
                }

                is UiAction.NavigateToMain -> {
                    navController.navigate(MainNavRoute.HOME.toString()) {
                        popUpTo(MainNavRoute.SPLASH.toString()) { inclusive = true }
                    }
                }
            }
        }
    }
    SplashScreenScreen(
        uiState = uiState,
    )
}

@Composable
fun SplashScreenScreen(
    uiState: SplashScreenUiState
) {
    Box(Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Splash background",
        )
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x60000000))
        )
        AnimatedVisibility(
            visible = uiState.showAppLogo,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + scaleIn(
                initialScale = 0.8f,
                transformOrigin = TransformOrigin.Center,
                animationSpec = tween(durationMillis = 500)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + scaleOut(
                targetScale = 0.8f,
                transformOrigin = TransformOrigin.Center,
                animationSpec = tween(durationMillis = 300)
            ),
            modifier = Modifier.align(BiasAlignment(0f, -.3f))
        ) {
            Text(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                color = Color.White,
                text = stringResource(id = R.string.app_name)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenScreenPreview() {
    SplashScreenScreen(uiState = SplashScreenUiState())
}
