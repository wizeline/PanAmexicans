package com.wizeline.panamexicans.presentation.main.map

import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.wizeline.panamexicans.BuildConfig
import com.wizeline.panamexicans.data.LocationPreferenceManager
import com.wizeline.panamexicans.data.directions.DirectionsRepository
import com.wizeline.panamexicans.data.models.UserStatus
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import com.wizeline.panamexicans.data.ridesessions.RideSessionStatus
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import com.wizeline.panamexicans.utils.toLatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val rideSessionsRepository: RideSessionRepository,
    private val userDataRepository: UserDataRepository,
    private val directionsRepository: DirectionsRepository,
    private val locationPreferenceManager: LocationPreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    private var rideSessionUsersJob: Job? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                locationPreferenceManager.saveLocation(it.latitude, it.longitude)
            }
            _uiState.update { it.copy(currentLocation = locationResult.lastLocation) }
        }
    }

    private val locationRequest = LocationRequest.Builder(
        /* intervalMillis = */ 10_000L
    ).apply {
        setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        setMinUpdateIntervalMillis(5_000L)
    }.build()

    init {
        val isAlreadyInSession = rideSessionsRepository.getConnectedSessionId()
        // connect directly
        startLocationUpdates()
        updatePreferenceLocationValues()
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            userData?.let {
                _uiState.update {
                    it.copy(
                        myId = userData.id,
                        firstName = userData.firstName.orEmpty(),
                        lastName = userData.lastName.orEmpty(),
                    )
                }
            }
        }
    }

    private fun updatePreferenceLocationValues() {
        val lastLocation = locationPreferenceManager.getLocation()
        lastLocation?.let {
            _uiState.update {
                it.copy(
                    cachedLocation = Location("gps").apply {
                        latitude = lastLocation.first
                        longitude = lastLocation.second
                    }
                )
            }
        }
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun onEvent(event: MapUiEvents) {
        when (event) {
            is MapUiEvents.OnPoiClicked -> {
                _uiState.update { it.copy(displayPoiDialog = true, lastPoiClicked = event.poi) }
            }

            is MapUiEvents.OnStartSharedSessionClicked -> {
                onStartSharedSessionClicked()
            }

            is MapUiEvents.OnTakeMeThereClicked -> {
                onTakeMeThereClicked(event)
            }

            is MapUiEvents.OnDismissDialog -> {
                _uiState.update { it.copy(displayPoiDialog = false, lastPoiClicked = null) }
            }

            else -> Unit
        }
    }

    private fun onStartSharedSessionClicked() {
        viewModelScope.launch {
            val user = userDataRepository.getUserData()
            val displayName =
                "${user?.firstName.orEmpty()} ${user?.lastName.orEmpty()}'s Ride Session"
            val initStatus = UserStatus(
                firstName = user?.firstName.orEmpty(),
                lastName = user?.lastName.orEmpty(),
                id = user?.id.orEmpty(),
                lat = _uiState.value.currentLocation?.latitude ?: 0.0,
                lon = _uiState.value.currentLocation?.longitude ?: 0.0,
                status = RideSessionStatus.RIDING.name
            )
            rideSessionsRepository.createRideSession(
                displayName,
                initStatus,
                onSuccess = { rideSessionId ->
                    subscribeToRideSessionUsers(rideSessionId)
                    _uiState.update {
                        it.copy(
                            sessionJointId = rideSessionId,
                            displayStartRideSession = false,
                            rideSessionTitle = displayName
                        )
                    }
                },
                onError = {})
        }
    }

    private fun subscribeToRideSessionUsers(rideSessionId: String) {
        rideSessionUsersJob?.cancel()
        rideSessionUsersJob = viewModelScope.launch {
            rideSessionsRepository.getRideSessionUsersFlow(rideSessionId).collect { users ->
                Log.d("TAG", "subscribeToRideSessionUsers: updatingValues")
                _uiState.update { it.copy(sessionUserStatus = users) }
            }
        }
    }

    private fun onTakeMeThereClicked(event: MapUiEvents.OnTakeMeThereClicked) {
        _uiState.update { it.copy(displayPoiDialog = false) }
        val alreadyInRoute = _uiState.value.routePoints != null
        viewModelScope.launch {
            val routePoints = _uiState.value.currentLocation?.toLatLng()?.let {
                directionsRepository.getRoute(
                    start = it,
                    end = event.poi.latLng,
                    apiKey = BuildConfig.MAPS_API_KEY
                )
            }
            _uiState.update { it.copy(routePoints = routePoints) }
            if (alreadyInRoute.not()) { // and not in a session
                _uiState.update { it.copy(displayStartRideSession = true) }
            }
        }
    }

    override fun onCleared() {
        leaveCurrentSession()
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun leaveCurrentSession() {
        rideSessionUsersJob?.cancel()
        rideSessionUsersJob = null
        _uiState.value.sessionJointId?.let { rideSessionId ->
            rideSessionsRepository.leaveRideSession(
                rideSessionId = rideSessionId,
                onSuccess = {
                    _uiState.update { it.copy(sessionJointId = null) }
                },
                onError = { exception ->
                }
            )
        }
    }
}

data class MapUiState(
    val myId: String? = null,
    val cachedLocation: Location? = null,
    val currentLocation: Location? = null,
    val firstName: String = "",
    val lastName: String = "",
    val status: String = "",
    val rideSessionTitle: String = "",
    val sessionJointId: String? = null,
    val isLoading: Boolean = false,
    val displayPoiDialog: Boolean = false,
    val lastPoiClicked: PointOfInterest? = null,
    val routePoints: List<LatLng>? = null,
    val displayStartRideSession: Boolean = false,
    val sessionUserStatus: List<UserStatus> = emptyList()
) {
    val otherRiders: List<UserStatus>
        get() = sessionUserStatus.filter { it.id != myId }
}

sealed interface MapUiEvents {
    data object OnDismissDialog : MapUiEvents
    data object OnStartSharedSessionClicked : MapUiEvents
    data class OnPoiClicked(val poi: PointOfInterest) : MapUiEvents
    data class OnTakeMeThereClicked(val poi: PointOfInterest) : MapUiEvents
}