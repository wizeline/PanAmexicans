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
import com.wizeline.panamexicans.data.SharedDataPreferenceManager
import com.wizeline.panamexicans.data.directions.DirectionsRepository
import com.wizeline.panamexicans.data.models.UserStatus
import com.wizeline.panamexicans.data.ridesessions.RideSessionRepository
import com.wizeline.panamexicans.data.ridesessions.RideSessionStatus
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import com.wizeline.panamexicans.data.voiceassistant.NavigationAction
import com.wizeline.panamexicans.data.voiceassistant.NavigationCommand
import com.wizeline.panamexicans.data.voiceassistant.VoiceAssistantRepository
import com.wizeline.panamexicans.presentation.crashdetector.CrashDetector
import com.wizeline.panamexicans.presentation.widget.PanAmexWidgetUiState
import com.wizeline.panamexicans.presentation.widget.PanAmexWidgetUpdater
import com.wizeline.panamexicans.utils.calculateDistanceInMeters
import com.wizeline.panamexicans.utils.metersToMiles
import com.wizeline.panamexicans.utils.toLatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val rideSessionsRepository: RideSessionRepository,
    private val userDataRepository: UserDataRepository,
    private val voiceAssistantRepository: VoiceAssistantRepository,
    private val directionsRepository: DirectionsRepository,
    private val sharedDataRepository: SharedDataRepository,
    private val preferenceManager: SharedDataPreferenceManager,
    private val crashDetector: CrashDetector,
    private val widget: PanAmexWidgetUpdater,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    private var rideSessionUsersJob: Job? = null

    init {
        collectCrashInformation()
        crashDetector.startListening()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.speed?.let {
                preferenceManager.saveSpeed(it)
            }
            val lastLocation = _uiState.value.currentLocation
            val distance = calculateDistanceInMeters(
                lastLocation?.latitude,
                lastLocation?.longitude,
                locationResult.lastLocation?.latitude,
                locationResult.lastLocation?.longitude
            )
            distance?.let {
                sharedDataRepository.addMiles(metersToMiles(distance).toFloat())
                updateWidget()
            }
            if (distance == null || distance < 10.0) return
            locationResult.lastLocation?.let { location ->

                preferenceManager.saveLocation(location.latitude, location.longitude)
                _uiState.value.sessionJointId?.let {
                    preferenceManager.joinSharedRide()
                    rideSessionsRepository.updateRideSessionStatus(
                        it,
                        UserStatus(
                            _uiState.value.firstName, _uiState.value.lastName,
                            id = _uiState.value.myId.orEmpty(),
                            lat = location.latitude,
                            lon = location.longitude,
                            status = _uiState.value.status
                        )
                    )
                }
                _uiState.update { it.copy(currentLocation = locationResult.lastLocation) }
            }
        }
    }

    private fun updateWidget() {
        val miles = sharedDataRepository.getMilesCounter()
        Log.d("WidgetDistance", "updateWidget: miles $miles")
        viewModelScope.launch {
            widget.update(uiState = PanAmexWidgetUiState(miles = miles.toInt()))
        }
    }

    private fun collectCrashInformation() {
        viewModelScope.launch {
            crashDetector.crashState.collect { crashState ->
                if (crashState.isCrashRisk.not()) return@collect
                _uiState.value.apply {
                    if (sessionJointId == null || currentLocation?.latitude == null || currentLocation?.longitude == null) return@collect
                    _uiState.update { it.copy(status = RideSessionStatus.DANGER.name) }
                    rideSessionsRepository.updateRideSessionStatus(
                        sessionJointId,
                        UserStatus(
                            _uiState.value.firstName, _uiState.value.lastName,
                            id = _uiState.value.myId.orEmpty(),
                            lat = currentLocation.latitude,
                            lon = currentLocation.longitude,
                            status = RideSessionStatus.DANGER.name
                        )
                    )
                }
            }
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
        updatePreferenceLocationValues()
        collectSharedDataInformation()
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            userData?.let {
                _uiState.update {
                    it.copy(
                        myId = userData.id,
                        firstName = userData.firstName.orEmpty(),
                        lastName = userData.lastName.orEmpty(),
                        status = RideSessionStatus.RIDING.name
                    )
                }
            }
        }
    }

    private fun collectSharedDataInformation() {
        viewModelScope.launch {
            sharedDataRepository.selectedRouteFlow.collectLatest { selectedRoute ->
                if (selectedRoute.isNotEmpty()) {
                    takeMeThere(selectedRoute)
                }
            }
        }
    }

    fun refreshSessionIfConnected() {
        val isAlreadyInSession = rideSessionsRepository.getConnectedSessionData()?.first
        if (isAlreadyInSession != null && isAlreadyInSession != _uiState.value.sessionJointId) {
            subscribeToRideSessionUsers(isAlreadyInSession)
        }
    }

    private fun updatePreferenceLocationValues() {
        val lastLocation = preferenceManager.getLocation()
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
            is MapUiEvents.OnToggleVoiceAssistant -> {
                if (_uiState.value.voiceAssistantEnabled) {
                    _uiState.update { it.copy(softListeningActive = true) }
                } else {
                    _uiState.update { it.copy(softListeningActive = false) }
                }
                _uiState.update { it.copy(voiceAssistantEnabled = !_uiState.value.voiceAssistantEnabled) }
            }

            is MapUiEvents.OnInstructionReceived -> {
                viewModelScope.launch {
                    val currentLocation =
                        if (_uiState.value.currentLocation?.latitude != null && _uiState.value.currentLocation?.longitude != null) LatLng(
                            _uiState.value.currentLocation?.latitude ?: 0.0,
                            _uiState.value.currentLocation?.longitude ?: 0.0
                        ) else null
                    val navigationCommand = voiceAssistantRepository.runVoiceCommand(
                        event.instruction,
                        BuildConfig.GEMINI_API_KEY,
                        currentLocation,
                    )
                    processCommand(navigationCommand)
                }
            }

            is MapUiEvents.OnStatusChanged -> {
                rideSessionsRepository.updateRideSessionStatus(
                    uiState.value.sessionJointId.orEmpty(),
                    UserStatus(
                        _uiState.value.firstName, _uiState.value.lastName,
                        id = _uiState.value.myId.orEmpty(),
                        lat = _uiState.value.currentLocation?.latitude ?: 0.0,
                        lon = _uiState.value.currentLocation?.longitude ?: 0.0,
                        status = event.status
                    )
                )
                _uiState.update { it.copy(status = event.status) }
            }

            is MapUiEvents.OnPoiClicked -> {
                _uiState.update { it.copy(displayPoiDialog = true, lastPoiClicked = event.poi) }
            }

            is MapUiEvents.OnStartSharedSessionClicked -> {
                onStartSharedSessionClicked()
            }

            is MapUiEvents.OnTakeMeThereClicked -> {
                takeMeThere(listOf(event.latlong))
            }

            is MapUiEvents.OnDangerClicked -> {
                _uiState.update {
                    it.copy(
                        displayDangerDialog = true,
                        lastDangerName = event.name,
                        lastDangerLatLng = event.targetPosition
                    )
                }
            }

            is MapUiEvents.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        displayPoiDialog = false,
                        lastPoiClicked = null,
                        lastDangerName = null,
                        lastDangerLatLng = null,
                        displayDangerDialog = false
                    )
                }
            }

            else -> Unit
        }
    }

    private fun processCommand(command: NavigationCommand?) {
        Log.d("VoiceService", "processCommand: $command")
        if (command == null) return
        when (command.action) {
            NavigationAction.ChangeStatusToDanger -> {
                _uiState.update { it.copy(status = RideSessionStatus.DANGER.name) }
                //active voice
            }

            NavigationAction.ChangeStatusToLunch -> {
                _uiState.update { it.copy(status = RideSessionStatus.LUNCH.name) }

            }

            NavigationAction.ChangeStatusToRiding -> {
                _uiState.update { it.copy(status = RideSessionStatus.RIDING.name) }

            }

            NavigationAction.ChangeStatusToBathroom -> {
                _uiState.update { it.copy(status = RideSessionStatus.BATHROOM.name) }

            }

            NavigationAction.TakeMeToNextDealer -> {
                if (command.lat != null && command.lon != null) {
                    takeMeThere(listOf(LatLng(command.lat, command.lon)))

                }
            }

            NavigationAction.TakeMeToNextGasStation -> {
                if (command.lat != null && command.lon != null) {
                    takeMeThere(listOf(LatLng(command.lat, command.lon)))

                }
            }

            NavigationAction.CancelNavigation -> {
                _uiState.update { it.copy(waypoints = null, routePoints = emptyList()) }

            }

            NavigationAction.SetDestination -> {
                if (command.lat != null && command.lon != null) {
                    takeMeThere(listOf(LatLng(command.lat, command.lon)))

                }
            }

            NavigationAction.UnknownCommand -> {
                Log.d("VoiceService", "processCommand: Could not process your command")
            }
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
            val sessionName = rideSessionsRepository.getConnectedSessionData()?.second
            sessionName?.let {
                _uiState.update {
                    it.copy(
                        rideSessionTitle = sessionName,
                        sessionJointId = rideSessionId
                    )
                }
            }
            rideSessionsRepository.getRideSessionUsersFlow(rideSessionId).catch { e ->
                Log.e("MapViewModel", "getRideSessionUsersFlow error: ${e.message} ")
            }.collect { users ->
                val dangerUsers = getDangerUsers(_uiState.value.sessionUserStatus, users)
                if (dangerUsers.isNotEmpty()) {
                    onEvent(
                        MapUiEvents.OnDangerClicked(
                            LatLng(
                                dangerUsers.first().lat,
                                dangerUsers.first().lon
                            ),
                            dangerUsers.first().firstName,
                        )
                    )
                }
                Log.d("TAG", "subscribeToRideSessionUsers: updatingValues")
                _uiState.update { it.copy(sessionUserStatus = users) }
            }
        }
    }

    private fun getDangerUsers(
        oldStatuses: List<UserStatus>,
        newStatuses: List<UserStatus>
    ): List<UserStatus> {
        val oldStatusMap = oldStatuses.associateBy { it.id }

        return newStatuses.filter { newUser ->
            newUser.id != _uiState.value.myId &&
                    newUser.status == RideSessionStatus.DANGER.name &&
                    (oldStatusMap[newUser.id]?.status != RideSessionStatus.DANGER.name)
        }
    }

    private fun takeMeThere(waypoints: List<LatLng>) {
        _uiState.update { it.copy(displayPoiDialog = false, displayDangerDialog = false) }
        val alreadyInRoute = _uiState.value.routePoints != null
        viewModelScope.launch {
            val routePoints = _uiState.value.currentLocation?.toLatLng()?.let {
                if (waypoints.size == 1) {
                    directionsRepository.getRoute(
                        start = it,
                        end = waypoints.first(),
                        apiKey = BuildConfig.MAPS_API_KEY
                    )
                } else {
                    directionsRepository.getRouteWithWaypoints(
                        start = it,
                        end = waypoints.last(),
                        waypoints = waypoints,
                        apiKey = BuildConfig.MAPS_API_KEY
                    )
                }
            }
            _uiState.update { it.copy(routePoints = routePoints, waypoints = waypoints) }
            if (alreadyInRoute.not()) { // and not in a session
                _uiState.update { it.copy(displayStartRideSession = true) }
            }
        }
    }

    override fun onCleared() {
        leaveCurrentSession()
        super.onCleared()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        crashDetector.stopListening()
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
    val statusOptions: List<RideSessionStatus> = enumValues<RideSessionStatus>().toList(),
    val firstName: String = "",
    val lastName: String = "",
    val status: String = "",
    val rideSessionTitle: String = "",
    val sessionJointId: String? = null,
    val isLoading: Boolean = false,
    val displayPoiDialog: Boolean = false,
    val displayDangerDialog: Boolean = false,
    val lastDangerLatLng: LatLng? = null,
    val lastDangerName: String? = null,
    val lastPoiClicked: PointOfInterest? = null,
    val softListeningActive: Boolean = false,
    val routePoints: List<LatLng>? = null,
    val waypoints: List<LatLng>? = null,
    val displayStartRideSession: Boolean = false,
    val sessionUserStatus: List<UserStatus> = emptyList(),
    val voiceAssistantEnabled: Boolean = false
) {
    val riders: List<UserStatus>
        get() = sessionUserStatus
}

sealed interface MapUiEvents {
    data object OnDismissDialog : MapUiEvents
    data object OnToggleVoiceAssistant : MapUiEvents
    data object OnCall911Clicked : MapUiEvents
    data object OnStartSharedSessionClicked : MapUiEvents

    data class OnInstructionReceived(val instruction: String) : MapUiEvents
    data class OnPoiClicked(val poi: PointOfInterest) : MapUiEvents
    data class OnTakeMeThereClicked(val latlong: LatLng) : MapUiEvents
    data class OnDangerClicked(val targetPosition: LatLng, val name: String) : MapUiEvents
    data class OnStatusChanged(val status: String) : MapUiEvents
}