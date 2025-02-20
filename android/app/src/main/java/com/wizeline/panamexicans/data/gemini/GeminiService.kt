package com.wizeline.panamexicans.data.gemini

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiService {

    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(
    val contents: List<Content>
)

data class GeminiResponse(
    val candidates: List<Candidate> = emptyList()
)

data class Candidate(
    val content: Content? = null
)

data class Content(
    val parts: List<Part>? = null,
    val role: String? = null
)

data class Part(
    val text: String? = null
)