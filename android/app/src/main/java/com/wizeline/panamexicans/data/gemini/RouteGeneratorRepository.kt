package com.wizeline.panamexicans.data.gemini

import com.google.gson.Gson
import com.wizeline.panamexicans.data.models.ChatBotResponse
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RouteGeneratorRepository @Inject constructor(
    private val service: GeminiService
) {
    suspend fun generateContent(
        prompt: String,
        preferences: List<String>,
        apiKey: String,
        latLon: Pair<Double, Double>?,
        hoursAvailable: Int
    ): ChatBotResponse {
        val initialPrompt = Content(
            parts = listOf(
                Part(
                    text = "\"You are a motorcycle tour planner. You receive a starting location (latitude and longitude), available tour hours, and a tour preference (Nature, Architecture, Historic Places, Adventure, Panoramic). Create an route to be driven by motorcycle that starts and ends at the given location, fitting the available time and preference. Include a summary message like 'We found this route for you!' that briefly describes the tour.\n" +
                            "\n" +
                            "Return your output in JSON using the following format:\n" +
                            "{\n" +
                            "  \"message\": \"Your summary message here\",\n" +
                            "  \"route\": [\n" +
                            "    {\n" +
                            "      \"lat\": 0.0,\n" +
                            "      \"lon\": 0.0\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}\n" +
                            "\n" +
                            "Ensure your response strictly follows this JSON format without any extra text.\""
                )
            ),
            role = "model"
        )
        val userPrompt = Content(
            parts = listOf(
                Part(prompt + "additionalData: { startLocation: {lat:${latLon?.first ?: 20.67904}, lon:${latLon?.second ?: -103.355649}}, preferences: ${preferences.ifEmpty { "The best places you know" }}, routeTime: ${if (hoursAvailable == 0) 2 else hoursAvailable}h }")
            ),
            role = "user"
        )
        val request = GeminiRequest(contents = listOf(initialPrompt, userPrompt))

        return try {
            val response = service.generateContent(apiKey, request)

            val jsonString = response.candidates.joinToString("\n") { candidate ->
                candidate.content?.parts?.joinToString(" ") { part ->
                    part.text?.trim().orEmpty()
                } ?: ""
            }
            val cleanedJsonString = jsonString
                .replace("```json", "")
                .replace("```", "")
                .replace("\n", " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            val gson = Gson()
            val chatBotResponse = gson.fromJson(cleanedJsonString, ChatBotResponse::class.java)
            chatBotResponse
        } catch (e: Exception) {
            "Error: ${e.message}"
            throw e
        }
    }
}