package com.wizeline.panamexicans.data.voiceassistant

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.wizeline.panamexicans.data.gemini.Content
import com.wizeline.panamexicans.data.gemini.GeminiRequest
import com.wizeline.panamexicans.data.gemini.GeminiResponse
import com.wizeline.panamexicans.data.gemini.GeminiService
import com.wizeline.panamexicans.data.gemini.Part
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceAssistantRepository @Inject constructor(
    private val service: GeminiService
) {
    suspend fun runVoiceCommand(
        voiceCommand: String,
        apiKey: String,
        latLon: LatLng?
    ): NavigationCommand? {
        val initialPrompt = Content(
            parts = listOf(
                Part(
                    text = "You are Jarvis, an advanced navigation assistant designed to help drivers navigate safely and efficiently. Your task is to interpret user commands and map them to predefined navigation actions. These actions include:\n" +
                            "- ChangeStatusToDanger: Switch the vehicle status to danger mode.\n" +
                            "- ChangeStatusToLunch: Switch the vehicle status to lunch mode.\n" +
                            "- ChangeStatusToBathroom: Switch the vehicle status to bathroom mode.\n" +
                            "- ChangeStatusToRiding: Switch the vehicle status to riding mode, for example when the rider is not in danger.\n" +
                            "- TakeMeToNextDealer: Navigate to the next Harley Davidson dealer location, using the provided current location only to find the closest dealer. Return dealer coordinates.\n" +
                            "- TakeMeToNextGasStation: Navigate to the next gas station, using the provided current location only to find the closest gas station. Return gas station coordinates.\n" +
                            "- CancelNavigation: Cancel the current navigation task.\n" +
                            "- SetDestination: Set a specific destination using the provided latitude and longitude. Return the destination coordinates.\n" +
                            "- UnknownCommand: When a command does not match any known patterns.\n" +
                            "\n" +
                            "When you receive a user instruction, analyze its content and try to map it to one of these actions. If the instruction does not match any known command, return the UnknownCommand action.\n" +
                            "\n" +
                            "Important: You will also receive the user's current location in a separate message formatted as: \n" +
                            "\"use my current location only to look for closer places {lat: <lat>, lon: <lon>}\"\n" +
                            "Use the current location ONLY to find nearby services (e.g., the next dealer or gas station) if applicable. However, if the user specifies an explicit destination in the command (for example, \"Take me to San Francisco Golden Gate\"), use the destination coordinates provided by the instruction and ignore the current location for that purpose.\n" +
                            "\n" +
                            "Return your response as a JSON object using the following structure:\n" +
                            "\n" +
                            "{\n" +
                            "  \"action\": \"ActionName\",\n" +
                            "  // If applicable, include the additional fields:\n" +
                            "  \"lat\": 0.0,\n" +
                            "  \"long\": 0.0\n" +
                            "}\n" +
                            "\n" +
                            "Your goal is to make navigation decisions clear, concise, and safe, responding with the appropriate action.\n"
                )
            ),
            role = "model"
        )
        val userPrompt = Content(
            parts = listOf(
                Part(voiceCommand + if (latLon != null) " use my current location only to look for closer places closer places {lat:${latLon.latitude}, lon:${latLon.longitude}}" else "")
            ),
            role = "user"
        )
        val request = GeminiRequest(contents = listOf(initialPrompt, userPrompt))

        return try {
            val response = service.generateContent(apiKey, request)
            return parseGeminiResponse(response)
        } catch (e: Exception) {
            "Error: ${e.message}"
            throw e
        }
    }
}

fun parseGeminiResponse(response: GeminiResponse): NavigationCommand? {
    // Obtén el primer candidate y su primer parte
    val candidate = response.candidates.firstOrNull() ?: return null
    val part = candidate.content?.parts?.firstOrNull() ?: return null
    val rawText = part.text

    val jsonRegex = "```json(.*?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
    val matchResult = jsonRegex.find(rawText.toString())
    val jsonString = matchResult?.groups?.get(1)?.value?.trim() ?: rawText?.trim()

    // Usa Gson para parsear el JSON al modelo NavigationCommand
    val gson = Gson()
    return gson.fromJson(jsonString, NavigationCommand::class.java)
}

enum class NavigationAction {
    ChangeStatusToDanger,
    ChangeStatusToLunch,
    ChangeStatusToBathroom,
    ChangeStatusToRiding,
    TakeMeToNextDealer,
    TakeMeToNextGasStation,
    CancelNavigation,
    SetDestination,
    UnknownCommand
}

data class NavigationCommand(
    val action: NavigationAction,
    val lat: Double? = null,
    @SerializedName("long")
    val lon: Double? = null
)