package com.wizeline.panamexicans.presentation.main.map

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MapRoot(modifier: Modifier = Modifier, viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun MapScreen(
    onEvent: (MapUiEvents) -> Unit,
    uiState: MapUiState
) {
    Box {

    }
}

@Preview
@Composable
private fun MapScreenPreview() {
    MapScreen(onEvent = {}, uiState = MapUiState())
}
