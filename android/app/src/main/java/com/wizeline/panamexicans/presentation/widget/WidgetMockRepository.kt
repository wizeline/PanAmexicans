package com.wizeline.panamexicans.presentation.widget

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WidgetMockRepository {

    companion object {

        private var instance: WidgetMockRepository? = null
        fun getInstance(): WidgetMockRepository =
            if (instance == null) WidgetMockRepository() else instance!!
    }

    private val _widgetState: MutableStateFlow<PanAmexWidgetState> =
        MutableStateFlow(PanAmexWidgetState.Loading)
    val widgetState: StateFlow<PanAmexWidgetState> = _widgetState.asStateFlow()

    suspend fun getMiles(): Int {
        val miles = (1..1000).random()
//        _widgetState.update { PanAmexWidgetState.OnRideMile(miles = miles) }
        return miles
    }

}

