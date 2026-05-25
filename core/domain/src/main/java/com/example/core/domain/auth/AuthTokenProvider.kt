package com.example.core.domain.auth

import kotlinx.coroutines.flow.Flow

/**
 * Provides access to the current authentication token.
 */
interface AuthTokenProvider {
    val tokenFlow: Flow<String?>

    fun getToken(): String?
}
