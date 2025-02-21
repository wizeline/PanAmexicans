package com.wizeline.panamexicans.data.directions

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class DirectionsRepositoryImpl @Inject constructor(private val directionsApi: DirectionsApi) :
    DirectionsRepository {

    override suspend fun getRouteWithWaypoints(
        start: LatLng,
        end: LatLng,
        waypoints: List<LatLng>,
        apiKey: String
    ): List<LatLng>? {
        try {
            val origin = "${start.latitude},${start.longitude}"
            val destination = "${end.latitude},${end.longitude}"
            val waypointsParam =
                waypoints.joinToString(separator = "|") { "${it.latitude},${it.longitude}" }

            val response = directionsApi.getDirectionsWithWaypoints(origin, destination, apiKey, waypointsParam)

            if (response.routes.isNotEmpty()) {
                val encodedPolyline = response.routes[0].overviewPolyline.points
                return decodePolyline(encodedPolyline)
            }
        } catch (e: Exception) {
            Log.e("Directions", "Error fetching directions with waypoints", e)
            return null
        }
        return null
    }

    override suspend fun getRoute(start: LatLng, end: LatLng, apiKey: String): List<LatLng>? {
        try {
            val origin = "${start.latitude},${start.longitude}"
            val destination = "${end.latitude},${end.longitude}"
            val response = directionsApi.getDirections(origin, destination, apiKey)
            if (response.routes.isNotEmpty()) {
                val encodedPolyline = response.routes[0].overviewPolyline.points
                return decodePolyline(encodedPolyline)
            }
        } catch (e: Exception) {
            Log.e("Directions", "Error fetching directions", e)
            return null
        }
        return null
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }
}