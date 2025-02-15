package com.wizeline.panamexicans.presentation.routecalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun RouteCalculatorRoot(navController: NavController, viewModel: RouteCalculatorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    RouteCalculatorScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun RouteCalculatorScreen(
    onEvent: (RouteCalculatorUiEvents) -> Unit,
    uiState: RouteCalculatorUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
private fun RouteCalculatorScreenPreview() {
    RouteCalculatorScreen(onEvent = {}, uiState = RouteCalculatorUiState())
}
