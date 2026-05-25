package com.example.core.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.core.domain.auth.AuthTokenProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Reads access token from DataStore and keeps an in-memory cached copy so callers
 * (e.g. OkHttp interceptors) can obtain it synchronously via [getToken].
 */
class DataStoreAuthTokenProvider
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : AuthTokenProvider {
        private val _token = MutableStateFlow<String?>(null)
        override val tokenFlow = _token.asStateFlow()

        override fun getToken(): String? = _token.value

        init {
            // Safe short-lived scope to collect DataStore updates and keep cached token.
            val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            val accessKey = stringPreferencesKey("access_token")
            scope.launch {
                dataStore.data.map { prefs -> prefs[accessKey] }.collect { value ->
                    _token.value = value
                }
            }
        }
    }
