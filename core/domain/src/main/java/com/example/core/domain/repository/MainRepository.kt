package com.example.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun parseRedirectUri(uri: android.net.Uri)

    fun isLoggedIn(): Flow<Boolean>
}
