package com.wizeline.panamexicans.presentation.voicerecognition

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun VoiceRecognitionRoot(navController: NavController, viewModel: VoiceRecognitionViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    VoiceRecognitionScreen(
        uiState = uiState,
        onEvent = {}
    )
}

@Composable
fun VoiceRecognitionScreen(
    onEvent: (VoiceRecognitionUiEvents) -> Unit,
    uiState: VoiceRecognitionUiState
) {
    Box {

    }
}

@Preview
@Composable
private fun VoiceRecognitionScreenPreview() {
    VoiceRecognitionScreen(onEvent = {}, uiState = VoiceRecognitionUiState())
}
