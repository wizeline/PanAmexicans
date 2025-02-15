package com.wizeline.panamexicans.presentation.iconupdater

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun IconUpdaterRoot(navController: NavController, viewModel: IconUpdaterViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    IconUpdaterScreen(
        uiState = uiState,
        onEvent = {}
    )
}

@Composable
fun IconUpdaterScreen(
    onEvent: (IconUpdaterUiEvents) -> Unit,
    uiState: IconUpdaterUiState
) {
    Box {

    }
}

@Preview
@Composable
private fun IconUpdaterScreenPreview() {
    IconUpdaterScreen(onEvent = {}, uiState = IconUpdaterUiState())
}
