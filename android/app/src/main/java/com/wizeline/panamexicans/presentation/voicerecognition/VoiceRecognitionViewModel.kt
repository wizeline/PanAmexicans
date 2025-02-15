package com.wizeline.panamexicans.presentation.voicerecognition

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceRecognitionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VoiceRecognitionUiState())
    val uiState: StateFlow<VoiceRecognitionUiState> = _uiState.asStateFlow()
}

data class VoiceRecognitionUiState(
    val isLoading: Boolean = false
)

sealed interface VoiceRecognitionUiEvents {
    data object OnClick : VoiceRecognitionUiEvents
}