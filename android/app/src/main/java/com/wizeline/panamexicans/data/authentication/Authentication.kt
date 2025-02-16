package com.wizeline.panamexicans.data.authentication

import com.google.firebase.auth.FirebaseUser

interface Authentication {
    fun getCurrentUser(): FirebaseUser?

    suspend fun login(email: String, password: String): FirebaseUser?

    suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): FirebaseUser?

    fun logout()
}