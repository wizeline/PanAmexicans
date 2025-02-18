package com.wizeline.panamexicans.presentation.main.map

import android.location.Location
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.PointOfInterest
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val userDataRepository: UserDataRepository
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
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
        startLocationUpdates()
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            userData?.let {
                _uiState.update {
                    it.copy(
                        firstName = userData.firstName.orEmpty(),
                        lastName = userData.lastName.orEmpty(),
                    )
                }
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

            is MapUiEvents.OnTakeMeThereClicked -> {
                _uiState.update { it.copy(displayPoiDialog = false) }
                // calculate route
            }

            is MapUiEvents.OnDismissDialog -> {
                _uiState.update { it.copy(displayPoiDialog = false, lastPoiClicked = null) }
            }

            else -> Unit
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Remove location updates to avoid leaks and unnecessary battery usage
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

data class MapUiState(
    val currentLocation: Location? = null,
    val firstName: String = "",
    val lastName: String = "",
    val status: String = "",
    val isLoading: Boolean = false,
    val displayPoiDialog: Boolean = false,
    val lastPoiClicked: PointOfInterest? = null
)

sealed interface MapUiEvents {
    data object OnDismissDialog : MapUiEvents
    data class OnPoiClicked(val poi: PointOfInterest) : MapUiEvents
    data class OnTakeMeThereClicked(val poi: PointOfInterest) : MapUiEvents
}