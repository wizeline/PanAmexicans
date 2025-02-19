package com.wizeline.panamexicans.presentation.main.sessions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.data.models.RideSession
import com.wizeline.panamexicans.data.models.UserStatus
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSessionsViewModel @Inject constructor(
    private val repository: RideSessionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapSessionsUiState())
    val uiState: StateFlow<MapSessionsUiState> = _uiState.asStateFlow()

    private var rideSessionUsersJob: Job? = null

    init {
        lookForSessions()
    }

    fun onEvent(event: MapSessionsUiEvents) {
        when (event) {
            is MapSessionsUiEvents.OnRefreshClicked -> {
                lookForSessions()
            }

            is MapSessionsUiEvents.OnSessionClicked -> {
                if (_uiState.value.sessionJointId != null) {
                    Log.d("TAG", "onEvent: already in a session")
                    leaveCurrentSession()
                    subscribeToRideSessionUsers(rideSessionId = event.sessionId, sessionName = event.sessionName)
                } else {
                    subscribeToRideSessionUsers(rideSessionId = event.sessionId, sessionName = event.sessionName)
                }
            }

            else -> Unit
        }
    }

    private fun lookForSessions() {
        viewModelScope.launch {
            getRideSessions()
        }
    }

    private fun subscribeToRideSessionUsers(rideSessionId: String, sessionName: String) {
        rideSessionUsersJob?.cancel()
        rideSessionUsersJob = viewModelScope.launch {
            repository.getRideSessionUsersFlow(rideSessionId).collect { users ->
                Log.d("TAG", "subscribeToRideSessionUsers: updatingValues")
                _uiState.update { it.copy(sessionUserStatus = users) }
            }
        }
        repository.setAsConnectedInSession(rideSessionId, sessionName)
    }

    private fun leaveCurrentSession() {
        rideSessionUsersJob?.cancel()
        rideSessionUsersJob = null

        _uiState.value.sessionJointId?.let { rideSessionId ->
            repository.leaveRideSession(
                rideSessionId = rideSessionId,
                onSuccess = {
                    _uiState.update { it.copy(sessionJointId = null) }
                },
                onError = { exception ->
                }
            )
        }
    }

    private fun getRideSessions() {
        viewModelScope.launch {
            repository.getRideSessions(
                onSuccess = { list ->
                    _uiState.update { it.copy(sessionList = list) }
                },
                onError = {}
            )
        }
    }
}

data class MapSessionsUiState(
    val sessionList: List<Pair<String, RideSession>> = emptyList(),
    val isLoading: Boolean = false,
    val sessionJointName: String = "",
    val sessionJointId: String? = null,
    val sessionUserStatus: List<UserStatus> = emptyList(),
)

sealed interface MapSessionsUiEvents {
    data object OnRefreshClicked : MapSessionsUiEvents
    data class OnSessionClicked(val sessionId: String, val sessionName: String) :
        MapSessionsUiEvents
}