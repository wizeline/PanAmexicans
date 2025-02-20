package com.wizeline.panamexicans.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}

data class ProfileUiState(
    val isLoading: Boolean = false
)

sealed interface ProfileUiEvents {
    data object OnClick : ProfileUiEvents
}