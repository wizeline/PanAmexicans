package com.wizeline.panamexicans.data.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.wizeline.panamexicans.data.FirebaseCollections
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseAuthentication"
private const val ID_DOC_FIELD = "id"
private const val FIRST_NAME_DOC_FIELD = "firstName"
private const val LAST_NAME_DOC_FIELD = "lastName"
private const val EMAIL_DOC_FIELD = "email"

class FirebaseAuthenticationImpl : Authentication {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun login(
        email: String,
        password: String
    ): FirebaseUser? =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "login: was successful")
                    continuation.resume(auth.currentUser)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }

    override suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): FirebaseUser? =
        suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "create account: was successful")
                    saveUser(auth.currentUser?.uid, firstName, lastName, email)
                    continuation.resume(auth.currentUser)
                }.addOnFailureListener {
                    Log.d(TAG, "create account failed")
                    continuation.resumeWithException(it)
                }
        }

    private fun saveUser(uid: String?, firstName: String, lastName: String, email: String) {
        if (uid == null) return
        val userData = hashMapOf(
            ID_DOC_FIELD to uid,
            FIRST_NAME_DOC_FIELD to firstName,
            LAST_NAME_DOC_FIELD to lastName,
            EMAIL_DOC_FIELD to email
        )

        firestore.collection(FirebaseCollections.USERS.name)
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("TAG", "saveUser: success")
            }
            .addOnFailureListener {
                Log.d("TAG", "saveUser: failure")
            }
    }

    override fun logout() {
        auth.signOut()
    }
}