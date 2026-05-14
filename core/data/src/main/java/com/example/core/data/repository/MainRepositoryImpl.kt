package com.example.core.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.core.data.di.DataModule.getValue
import com.example.core.data.di.DataModule.setValue
import com.example.core.domain.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class MainRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : MainRepository {
        val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)

        private suspend fun setAccessToken(value: String) {
            dataStore.setValue(ACCESS_TOKEN_KEY, value)
            accessToken.map { it != null }
        }

        override suspend fun parseRedirectUri(uri: Uri) {
            val dummyUrl = Uri.parse("http://dummyurl.com?${uri.fragment}")
            dummyUrl.getQueryParameter("access_token")?.let { token ->
                setAccessToken(token)
                // todo: add/update user details
            }
        }

        override fun isLoggedIn(): Flow<Boolean> = accessToken.map { it != null }

        companion object {
            private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        }
    }
