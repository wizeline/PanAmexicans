package com.wizeline.panamexicans.presentation.ridesessions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.data.models.RideSession
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import com.wizeline.panamexicans.data.models.UserStatus
import com.wizeline.panamexicans.data.ridesessions.RideSessionStatus
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import com.wizeline.panamexicans.utils.getRandomCoordinateInSanFrancisco
import com.wizeline.panamexicans.utils.getRandomLatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RideSessionsViewModel @Inject constructor(
    private val repository: RideSessionRepository,
    private val userRepository: UserDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RideSessionsUiState())
    val uiState: StateFlow<RideSessionsUiState> = _uiState.asStateFlow()

    private var rideSessionUsersJob: Job? = null

    fun onEvent(event: RideSessionsUiEvents) {
        when (event) {
            is RideSessionsUiEvents.OnGenerateSessionClicked -> {
                generateSession()
            }

            is RideSessionsUiEvents.OnLookForSessionsClicked -> {
                if (_uiState.value.sessionJointId != null) {
                    Log.d("TAG", "onEvent: already in a session")
                    viewModelScope.launch {
                        leaveCurrentSession()
                        delay(1000)
                        getRideSessions()
                    }
                } else {
                    getRideSessions()
                }
            }

            is RideSessionsUiEvents.OnSessionClicked -> {
                if (_uiState.value.sessionJointId != null) {
                    Log.d("TAG", "onEvent: already in a session")
                    leaveCurrentSession()
                    joinToSession(event.sessionId, event.sessionName)
                    subscribeToRideSessionUsers(rideSessionId = event.sessionId)
                } else {
                    joinToSession(event.sessionId, event.sessionName)
                    subscribeToRideSessionUsers(rideSessionId = event.sessionId)
                }
            }

            else -> Unit
        }
    }

    private fun subscribeToRideSessionUsers(rideSessionId: String) {
        rideSessionUsersJob?.cancel()
        rideSessionUsersJob = viewModelScope.launch {
            repository.getRideSessionUsersFlow(rideSessionId).collect { users ->
                Log.d("TAG", "subscribeToRideSessionUsers: updatingValues")
                _uiState.update { it.copy(sessionUserStatus = users) }
            }
        }
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

    private fun joinToSession(sessionId: String, sessionName: String) {
        viewModelScope.launch {
            val userData = userRepository.getUserData()
            repeat(10) {
                val randomLatLong = getRandomCoordinateInSanFrancisco()
                val userStatus = UserStatus(
                    firstName = userData?.firstName.orEmpty(),
                    lastName = userData?.lastName.orEmpty(),
                    id = userData?.id.orEmpty(),
                    lat = randomLatLong.latitude,
                    lon = randomLatLong.longitude,
                    status = RideSessionStatus.RIDING.name
                )
                repository.updateRideSessionStatus(sessionId,
                    userStatus,
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                rideSessionContainerState = RideSessionContainerState.ON_SESSION,
                                sessionJointId = sessionId,
                                sessionJointName = sessionName
                            )
                        }
                    },
                    onError = {})
                delay(2000)
            }
        }
    }

    private fun getRideSessions() {
        viewModelScope.launch {
            repository.getRideSessions(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            rideSessionContainerState = RideSessionContainerState.SESSION_LIST,
                            sessionList = list
                        )
                    }
                },
                onError = {}
            )
        }
    }

    private fun generateSession() {
        viewModelScope.launch {
            val userData = userRepository.getUserData()
            val displayName = "${userData?.firstName} ${userData?.lastName}'s Ride Session"
            repository.createRideSession(
                displayName = displayName,
                onSuccess = { sessionId ->
                    _uiState.update {
                        it.copy(
                            sessionJointId = sessionId,
                            sessionJointName = displayName,
                            rideSessionContainerState = RideSessionContainerState.ON_SESSION
                        )
                    }
                    joinToSession(sessionId, displayName)
                    subscribeToRideSessionUsers(rideSessionId = sessionId)
                },
                onError = { // TODO:
                },
                initStatus = UserStatus(
                    firstName = userData?.firstName.orEmpty(),
                    lastName = userData?.lastName.orEmpty(),
                    id = userData?.id.orEmpty(),
                    lat = 0.123123,
                    lon = 0.2343123,
                    status = RideSessionStatus.RIDING.name
                )
            )
        }
    }
}

data class RideSessionsUiState(
    val isLoading: Boolean = false,
    val sessionJointName: String = "",
    val sessionJointId: String? = null,
    val sessionList: List<Pair<String, RideSession>> = emptyList(),
    val sessionUserStatus: List<UserStatus> = emptyList(),
    val rideSessionContainerState: RideSessionContainerState = RideSessionContainerState.NONE
) {
    val generateSessionEnabled : Boolean
        get() = rideSessionContainerState != RideSessionContainerState.ON_SESSION
}

enum class RideSessionContainerState {
    NONE,
    ON_SESSION,
    SESSION_LIST
}

sealed interface RideSessionsUiEvents {
    data object OnGenerateSessionClicked : RideSessionsUiEvents
    data object OnLookForSessionsClicked : RideSessionsUiEvents
    data class OnSessionClicked(val sessionId: String, val sessionName: String) :
        RideSessionsUiEvents
}