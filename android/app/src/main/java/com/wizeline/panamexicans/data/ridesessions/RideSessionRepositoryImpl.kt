package com.wizeline.panamexicans.data.ridesessions

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.FirebaseCollections
import com.wizeline.panamexicans.data.models.RideSession
import com.wizeline.panamexicans.data.models.UserStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RideSessionRepositoryImpl() : RideSessionRepository {
    override val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var connectedRideSessionId: String? = null

    override fun getConnectedSessionId() = connectedRideSessionId

    override fun createRideSession(
        displayName: String,
        initStatus: UserStatus,
        onSuccess: (rideSessionId: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }
        val rideSession = RideSession(
            rideSessionName = displayName,
            creator = currentUser.uid,
            createdAt = Timestamp.now()
        )

        db.collection(FirebaseCollections.RIDE_SESSIONS.name)
            .add(rideSession)
            .addOnSuccessListener { documentReference ->
                connectedRideSessionId = documentReference.id
                db.collection(FirebaseCollections.RIDE_SESSIONS.name)
                    .document(documentReference.id)
                    .collection(FirebaseCollections.USERS.name)
                    .document(currentUser.uid)
                    .set(initStatus)
                    .addOnSuccessListener {
                        onSuccess(documentReference.id)
                    }
                    .addOnFailureListener { e ->
                        onError(e)
                    }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    override fun updateRideSessionStatus(
        rideSessionId: String,
        userStatus: UserStatus,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }
        db.collection(FirebaseCollections.RIDE_SESSIONS.name)
            .document(rideSessionId)
            .collection(FirebaseCollections.USERS.name)
            .document(currentUser.uid)
            .set(userStatus)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e) }
    }

    override fun getRideSessionUsersFlow(rideSessionId: String): Flow<List<UserStatus>> =
        callbackFlow {
            val listenerRegistration = db.collection(FirebaseCollections.RIDE_SESSIONS.name)
                .document(rideSessionId)
                .collection(FirebaseCollections.USERS.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val users = snapshot?.toObjects(UserStatus::class.java) ?: emptyList()
                    trySend(users).isSuccess
                }
            awaitClose { listenerRegistration.remove() }
        }

    override fun getRideSessions(
        onSuccess: (List<Pair<String, RideSession>>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.RIDE_SESSIONS.name)
            .get()
            .addOnSuccessListener { snapshot ->
                val sessions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(RideSession::class.java)?.let { rideSession ->
                        doc.id to rideSession
                    }
                }
                onSuccess(sessions)
            }
            .addOnFailureListener { e -> onError(e) }
    }

    override fun leaveRideSession(
        rideSessionId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }
        val db = FirebaseFirestore.getInstance()
        val sessionRef =
            db.collection(FirebaseCollections.RIDE_SESSIONS.name).document(rideSessionId)
        val userRef =
            sessionRef.collection(FirebaseCollections.USERS.name).document(currentUser.uid)

        userRef.delete()
            .addOnSuccessListener {
                connectedRideSessionId = null
                sessionRef.collection(FirebaseCollections.USERS.name).get()
                    .addOnSuccessListener { snapshot ->
                        if (snapshot.isEmpty) {
                            sessionRef.delete()
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { e -> onError(e) }
                        } else {
                            onSuccess()
                        }
                    }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { e -> onError(e) }
    }
}