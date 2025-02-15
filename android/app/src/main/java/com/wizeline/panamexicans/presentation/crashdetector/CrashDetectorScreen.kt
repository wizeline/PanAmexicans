package com.wizeline.panamexicans.presentation.crashdetector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun CrashDetectorRoot(navController: NavController, viewModel: CrashDetectorViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    CrashDetectorScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun CrashDetectorScreen(
    onEvent: (CrashDetectorUiEvents) -> Unit,
    uiState: CrashDetectorUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview(showBackground = true)
@Composable
private fun CrashDetectorScreenPreview() {
    CrashDetectorScreen(onEvent = {}, uiState = CrashDetectorUiState())
}
