package com.wizeline.panamexicans.data.models

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class ChatBotResponse(
    val message: String,
    val route: List<BasicWaypoint>? = null
) {
    fun toChatBotResponseWithRouteImage(image: String?): ChatBotResponseWithRouteImage {
        return ChatBotResponseWithRouteImage(
            message = message,
            route = GeneratedRoutImage(image),
            waypoints = route?.map { LatLng(it.lat, it.lon) }.orEmpty()
        )
    }
}

@Serializable
data class BasicWaypoint(
    val lat: Double,
    val lon: Double
)

data class ChatBotResponseWithRouteImage(
    val message: String,
    val route: GeneratedRoutImage? = null,
    val waypoints: List<LatLng> = emptyList()
)

@Serializable
data class GeneratedRoutImage(
    val routeImage: String?
)
