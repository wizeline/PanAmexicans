package com.wizeline.panamexicans.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.userdata.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userDataRepository: UserDataRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userData = userDataRepository.getUserData()
            userData?.let {
                _uiState.update {
                    it.copy(
                        firstName = userData.firstName.orEmpty(),
                        lastName = userData.lastName.orEmpty()
                    )
                }
            }
        }
    }

    fun onEvent(event: MainUiEvents) {
        when (event) {
            is MainUiEvents.OnTabClicked -> {
                _uiState.update { it.copy(selectedTab = event.screenName) }
            }

            else -> Unit
        }
    }
}

data class MainUiState(
    val firstName: String = "",
    val lastName: String = "",
    val selectedTab: String = MainTabs.Map.name,
    val bottomNavScreens: List<Pair<String, Int>> = listOf(
        MainTabs.Sessions.name to R.drawable.ic_group,
        MainTabs.Map.name to R.drawable.ic_map,
        MainTabs.RouteGenerator.name to R.drawable.ic_route
    )
)

enum class MainTabs {
    Sessions,
    Map,
    RouteGenerator
}

sealed interface MainUiEvents {
    data object OnProfileClicked : MainUiEvents
    data class OnTabClicked(val screenName: String) : MainUiEvents
}