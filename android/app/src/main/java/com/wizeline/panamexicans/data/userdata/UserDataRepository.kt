package com.wizeline.panamexicans.data.userdata

import com.wizeline.panamexicans.data.models.UserData

interface UserDataRepository {
    suspend fun getUserData(): UserData?
    fun clearCacheValues()
}