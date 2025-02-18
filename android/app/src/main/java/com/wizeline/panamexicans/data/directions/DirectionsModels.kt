package com.wizeline.panamexicans.data.directions

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class POI(
    val name: String,
    val latLng: LatLng
)

data class DirectionsResponse(
    @SerializedName("routes")
    val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline
)

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)