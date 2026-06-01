package com.example.core.domain.repository

import com.example.core.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getUserDetails(): User

    suspend fun removeUserDetails()

    suspend fun parseRedirectUri(uri: android.net.Uri)

    fun isLoggedIn(): Flow<Boolean>
}
