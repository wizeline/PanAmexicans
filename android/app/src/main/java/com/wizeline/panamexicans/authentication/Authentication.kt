package com.wizeline.panamexicans.authentication

import com.google.firebase.auth.FirebaseUser

interface Authentication {
    fun getCurrentUser(): FirebaseUser?
    suspend fun login(email: String, password: String): FirebaseUser?
    suspend fun createAccount(email: String, password: String): FirebaseUser?
    fun logout()
}