package com.wizeline.panamexicans.data.shareddata

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface SharedDataRepository {
    suspend fun setSelectedRoute(routePoints: List<LatLng>?)
    fun addMiles(miles: Float)
    fun getMilesCounter(): Float
    val selectedRouteFlow: StateFlow<List<LatLng>>
}