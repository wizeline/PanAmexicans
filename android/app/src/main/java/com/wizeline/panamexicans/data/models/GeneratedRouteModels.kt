package com.wizeline.panamexicans.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatBotResponse(
    val message: String,
    val route: List<BasicWaypoint>? = null
) {
    fun toChatBotResponseWithRouteImage(image: String?): ChatBotResponseWithRouteImage {
        return ChatBotResponseWithRouteImage(
            message,
            GeneratedRoutImage(image)
        )
    }
}

@Serializable
data class BasicWaypoint(
    val lat: Double,
    val lon: Double
)

@Serializable
data class ChatBotResponseWithRouteImage(
    val message: String,
    val route: GeneratedRoutImage? = null
)

@Serializable
data class GeneratedRoutImage(
    val routeImage: String?
)
