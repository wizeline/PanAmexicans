package com.wizeline.panamexicans.data.shareddata

import com.google.android.gms.maps.model.LatLng
import com.wizeline.panamexicans.data.SharedDataPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedDataRepositoryImpl @Inject constructor(
    private val preferenceManager: SharedDataPreferenceManager
) : SharedDataRepository {
    private val _selectedRouteFlow: MutableStateFlow<List<LatLng>> = MutableStateFlow(emptyList())
    override val selectedRouteFlow: StateFlow<List<LatLng>> get() = _selectedRouteFlow

    override fun addMiles(miles: Float) {
        if (miles > 500) return
        val currentMiles = preferenceManager.getMilesCounter()
        val newMiles = currentMiles + miles
        preferenceManager.storeMiles(newMiles)
    }

    override fun getMilesCounter(): Float {
        return preferenceManager.getMilesCounter()
    }

    override suspend fun setSelectedRoute(routePoints: List<LatLng>?) {
        routePoints?.let {
            _selectedRouteFlow.emit(routePoints)
        }
    }

}