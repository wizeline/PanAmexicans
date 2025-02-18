package com.wizeline.panamexicans.data.ridesessions

import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.models.RideSession
import com.wizeline.panamexicans.data.models.UserStatus
import kotlinx.coroutines.flow.Flow

interface RideSessionRepository {
    val db: FirebaseFirestore

    fun createRideSession(
        displayName: String,
        initStatus: UserStatus,
        onSuccess: (rideSessionId: String) -> Unit,
        onError: (Exception) -> Unit
    )

    fun updateRideSessionStatus(
        rideSessionId: String,
        userStatus: UserStatus,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    )

    fun getRideSessions(
        onSuccess: (List<Pair<String, RideSession>>) -> Unit,
        onError: (Exception) -> Unit = {}
    )

    fun leaveRideSession(
        rideSessionId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    fun getRideSessionUsersFlow(rideSessionId: String): Flow<List<UserStatus>>

    fun getConnectedSessionId(): String?
}