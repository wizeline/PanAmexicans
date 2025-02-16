package com.wizeline.panamexicans.data.models

import com.google.firebase.Timestamp

data class UserStatus(
    val firstName: String = "",
    val lastName: String = "",
    val id: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val status: String = "",
    val updatedAt: Timestamp = Timestamp.now()
)