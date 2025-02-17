package com.wizeline.panamexicans.presentation.iconupdater

import androidx.lifecycle.ViewModel
import com.wizeline.panamexicans.data.subscription.SubscriptionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class IconUpdaterViewModel @Inject constructor(
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(IconUpdaterUiState())
    val uiState: StateFlow<IconUpdaterUiState> = _uiState.asStateFlow()

    init {
        val isSubscribed = subscriptionManager.getSubscriptionStatus()
        _uiState.update { it.copy(isSubscribed = isSubscribed) }
    }

    fun onEvent(event: IconUpdaterUiEvents) {
        when (event) {
            is IconUpdaterUiEvents.OnToggleIconClicked -> {
                subscriptionManager.setSubscriptionStatus(!uiState.value.isSubscribed)
                _uiState.update { it.copy(isSubscribed = !uiState.value.isSubscribed) }
            }
        }
    }
}

data class IconUpdaterUiState(
    val isLoading: Boolean = false,
    val isSubscribed: Boolean = false
)

sealed interface IconUpdaterUiEvents {
    data class OnToggleIconClicked(val enabled: Boolean) : IconUpdaterUiEvents
}