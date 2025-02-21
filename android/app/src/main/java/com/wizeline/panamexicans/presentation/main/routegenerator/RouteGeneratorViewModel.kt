package com.wizeline.panamexicans.presentation.main.routegenerator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.wizeline.panamexicans.BuildConfig
import com.wizeline.panamexicans.data.SharedDataPreferenceManager
import com.wizeline.panamexicans.data.gemini.RouteGeneratorRepository
import com.wizeline.panamexicans.data.models.Author
import com.wizeline.panamexicans.data.models.BasicWaypoint
import com.wizeline.panamexicans.data.models.ChatBotResponseWithRouteImage
import com.wizeline.panamexicans.data.models.ChatMessage
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteGeneratorViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val routeGeneratorRepository: RouteGeneratorRepository,
    private val locationPreferenceManager: SharedDataPreferenceManager,
    private val sharedDataRepository: SharedDataRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RouteGeneratorUiState())
    val uiState: StateFlow<RouteGeneratorUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(selectedStates = _uiState.value.preferences.map { it to false }) }
        viewModelScope.launch {
            val userName = userDataRepository.getUserData()?.firstName.orEmpty()

            val initialChatBotMenu = ChatMessage(
                response = ChatBotResponseWithRouteImage("¡Hello $userName! I'm your AI assistant to help you create a dynamic route based on your preferences"),
                author = Author.Bot
            )
            val menuList = listOf(
                ChatMessage(
                    response = ChatBotResponseWithRouteImage(""),
                    prompt = "",
                    author = Author.Preferences
                )
            )
            _uiState.update { it.copy(messages = mutableListOf(initialChatBotMenu) + menuList) }
        }
    }

    fun onEvent(event: RouteGeneratorUiEvents) {
        when (event) {
            is RouteGeneratorUiEvents.OnAvailableHoursChanged -> {
                _uiState.update { it.copy(availableHours = event.hours.toIntOrNull() ?: 0) }
            }

            is RouteGeneratorUiEvents.OnTakeMeThereClicked -> {
                viewModelScope.launch {
                    sharedDataRepository.setSelectedRoute(event.waypoints)
                }
            }

            is RouteGeneratorUiEvents.OnMessageSent -> {
                sendMessage(
                    userMessage = event.userMessage,
                    preferences = _uiState.value.selectedStates.filter { it.second }
                        .map { it.first })
            }

            is RouteGeneratorUiEvents.OnPreferenceClicked -> {
                val editableList = _uiState.value.selectedStates.toMutableList()
                editableList[event.index] =
                    editableList[event.index].copy(second = !editableList[event.index].second)
                _uiState.update { currentState ->
                    currentState.copy(selectedStates = editableList)
                }
            }

            else -> Unit
        }
    }

    private fun sendMessage(
        userMessage: String,
        usingPredefinedPrompt: Boolean = false,
        preferences: List<String>
    ) {
        viewModelScope.launch {
            val lastLocation = locationPreferenceManager.getLocation()

            try {
                val response = routeGeneratorRepository.generateContent(
                    prompt = userMessage,
                    latLon = lastLocation,
                    preferences = preferences,
                    hoursAvailable = uiState.value.availableHours,
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
                val routeImage = response.route?.let { convertWaypointsToImage(it) }

                val newList = mutableListOf<ChatMessage>()
                newList.addAll(_uiState.value.messages)
                if (usingPredefinedPrompt.not()) newList.add(
                    ChatMessage(
                        response = ChatBotResponseWithRouteImage(userMessage),
                        author = Author.Me
                    )
                )
                newList.add(
                    ChatMessage(
                        response = response.toChatBotResponseWithRouteImage(routeImage),
                        author = Author.Bot
                    )
                )

                _uiState.update {
                    it.copy(messages = newList)
                }
            } catch (ex: Exception) {

            }
        }
    }

    private fun convertWaypointsToImage(waypoints: List<BasicWaypoint>): String {
        val apiKey = BuildConfig.MAPS_API_KEY
        val baseUrl = "https://maps.googleapis.com/maps/api/staticmap"
        val size = "600x600"
        val mapType = "roadmap"  // O puedes probar con "satellite", "hybrid", etc.
        val scale = 2

        val waypointsToParse = waypoints.map { it.lat to it.lon }

        val markers =
            waypointsToParse.joinToString("&markers=color:blue|") { "${it.first},${it.second}" }
        val markerParam = "markers=color:red|$markers"

        val path = "path=color:0x0000ff|weight:5|" +
                waypointsToParse.joinToString("|") { "${it.first},${it.second}" }

        val url = "$baseUrl?size=$size&scale=$scale&maptype=$mapType&$markerParam&$path&key=$apiKey"
        return url
    }
}

data class RouteGeneratorUiState(
    val userName: String = "",
    val isLoading: Boolean = false,
    val preferences: List<String> = listOf(
        "Nature",
        "Architecture",
        "Historic Places",
        "Adventure",
        "Panoramic"
    ),
    val selectedStates: List<Pair<String, Boolean>> = emptyList(),
    val availableHours: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

sealed interface RouteGeneratorUiEvents {
    data class OnMessageSent(val userMessage: String) : RouteGeneratorUiEvents
    data class OnPreferenceClicked(val index: Int) : RouteGeneratorUiEvents
    data class OnTakeMeThereClicked(val waypoints: List<LatLng>) : RouteGeneratorUiEvents
    data class OnAvailableHoursChanged(val hours: String) : RouteGeneratorUiEvents
}