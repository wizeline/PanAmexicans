package com.wizeline.panamexicans.data.directions

import com.google.android.gms.maps.model.LatLng

interface DirectionsRepository {

    suspend fun getRoute(start: LatLng, end: LatLng, apiKey: String): List<LatLng>?
}