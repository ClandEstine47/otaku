package com.example.core.navigation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StartDestination(private val context: Context) {
    // to make sure there's only one instance
    companion object {
        private val Context.dataStoree: DataStore<Preferences> by preferencesDataStore("startRoute")
        val START_ROUTE_KEY = stringPreferencesKey("start_route")
    }

    // get the saved route
    val getRoute: Flow<String> =
        context.dataStoree.data
            .map { preferences ->
                preferences[START_ROUTE_KEY] ?: OtakuScreen.AnimeTab.toString()
            }

    // save route into datastore
    suspend fun saveRoute(name: String) {
        context.dataStoree.edit { preferences ->
            preferences[START_ROUTE_KEY] = name
        }
    }
}
