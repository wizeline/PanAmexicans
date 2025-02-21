package com.wizeline.panamexicans.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.data.SharedDataPreferenceManager
import com.wizeline.panamexicans.data.authentication.Authentication
import com.wizeline.panamexicans.data.models.UserData
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import com.wizeline.panamexicans.data.subscription.SubscriptionManager
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val authentication: Authentication,
    private val subscriptionManager: SubscriptionManager,
    private val sharedDataRepository: SharedDataRepository,
    private val preferenceManager: SharedDataPreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private val _uiAction = MutableSharedFlow<ProfileUiAction>()
    val uiAction: SharedFlow<ProfileUiAction> = _uiAction.asSharedFlow()

    init {
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            userData?.let {
                _uiState.update { it.copy(userData = userData) }
            }
        }
        preferenceManager.getHasJoinedSharedRide().let { hasJoinedSharedSession ->
            _uiState.update { it.copy(hasJoinedSharedSession = hasJoinedSharedSession) }
        }
        subscriptionManager.getSubscriptionStatus().let { isPremium ->
            _uiState.update { it.copy(isPremium = isPremium) }
        }
    }

    fun onEvent(event: ProfileUiEvents) {
        when (event) {
            is ProfileUiEvents.OnTooglePremium -> {
                val isPremium = !_uiState.value.isPremium
                _uiState.update { it.copy(isPremium = isPremium) }
                subscriptionManager.setSubscriptionStatus(isPremium)
            }

            is ProfileUiEvents.OnLogoutClicked -> {
                authentication.logout()
                userDataRepository.clearCacheValues()
                viewModelScope.launch {
                    _uiAction.emit(ProfileUiAction.OnAccountLoggedOut)
                }
            }

            else -> Unit
        }
    }
}

sealed class ProfileUiAction {
    data object OnAccountLoggedOut : ProfileUiAction()
}

data class ProfileUiState(
    val userData: UserData? = null,
    val isLoading: Boolean = false,
    val isPremium: Boolean = false,
    val hasJoinedSharedSession: Boolean = false,
)

sealed interface ProfileUiEvents {
    data object OnLogoutClicked : ProfileUiEvents
    data object OnTooglePremium : ProfileUiEvents
}