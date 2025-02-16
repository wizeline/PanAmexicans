package com.wizeline.panamexicans.data.userdata

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.FirebaseCollections
import com.wizeline.panamexicans.data.models.UserData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val authentication: FirebaseAuth,
    private val db: FirebaseFirestore
) : UserDataRepository {

    override suspend fun getUserData(): UserData? {
        val currentUser = authentication.currentUser ?: return null
        return try {
            val documentSnapshot = db.collection(FirebaseCollections.USERS.name)
                .document(currentUser.uid)
                .get()
                .await()

            documentSnapshot.toObject(UserData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}