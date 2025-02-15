package com.wizeline.panamexicans.presentation.ridesessions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RideSessionsRoot(navController: NavController, viewModel: RideSessionsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    RideSessionsScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun RideSessionsScreen(
    onEvent: (RideSessionsUiEvents) -> Unit,
    uiState: RideSessionsUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
private fun RideSessionsScreenPreview() {
    RideSessionsScreen(onEvent = {}, uiState = RideSessionsUiState())
}
