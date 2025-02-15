package com.wizeline.panamexicans.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetectorViewModel

@Composable
fun MainRoot(navController: NavController, viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun MainScreen(
    onEvent: (MainUiEvents) -> Unit,
    uiState: MainUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(onEvent = {}, uiState = MainUiState())
}
