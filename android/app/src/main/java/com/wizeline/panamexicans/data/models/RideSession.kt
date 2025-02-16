package com.wizeline.panamexicans.data.models

import com.google.firebase.Timestamp

data class RideSession(
    val rideSessionName: String = "",
    val creator: String = "",
    val createdAt: Timestamp = Timestamp.now()
)