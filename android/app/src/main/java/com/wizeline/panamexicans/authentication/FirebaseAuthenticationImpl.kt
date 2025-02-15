package com.wizeline.panamexicans.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "FirebaseAuthentication"

class FirebaseAuthenticationImpl : Authentication {
    private val auth = FirebaseAuth.getInstance()

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun login(email: String, password: String): FirebaseUser? =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "login: was successful")
                    continuation.resume(auth.currentUser)
                }.addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }

    override suspend fun createAccount(email: String, password: String): FirebaseUser? =
        suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Log.d(TAG, "create account: was successful")
                    continuation.resume(auth.currentUser)
                }.addOnFailureListener {
                    Log.d(TAG, "create account failed")
                    continuation.resumeWithException(it)
                }
        }

    override fun logout() {
        auth.signOut()
    }
}