package com.example.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.apollographql.apollo.ApolloClient
import com.example.core.data.auth.DataStoreAuthTokenProvider
import com.example.core.data.repository.MainRepositoryImpl
import com.example.core.domain.auth.AuthTokenProvider
import com.example.core.domain.repository.MainRepository
import com.example.core.domain.service.MediaService
import com.example.core.network.service.MediaServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private const val DEFAULT_PREFERENCES = "default.preferences_pb"

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideMediaService(apolloClient: ApolloClient): MediaService = MediaServiceImpl(apolloClient)

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext appContext: Context,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { appContext.filesDir.resolve(DEFAULT_PREFERENCES) },
        )

    fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>): Flow<T?> = data.map { it[key] }

    fun <T> DataStore<Preferences>.getValue(
        key: Preferences.Key<T>,
        default: T,
    ): Flow<T> = data.map { it[key] ?: default }

    suspend fun <T> DataStore<Preferences>.setValue(
        key: Preferences.Key<T>,
        value: T?,
    ) = edit {
        if (value != null) {
            it[key] = value
        } else {
            it.remove(key)
        }
    }

    @Provides
    @Singleton
    fun provideMainRepository(
        mediaService: MediaService,
        dataStore: DataStore<Preferences>,
    ): MainRepository = MainRepositoryImpl(mediaService = mediaService, dataStore = dataStore)

    @Provides
    @Singleton
    fun provideAuthTokenProvider(
        dataStore: DataStore<Preferences>,
    ): AuthTokenProvider = DataStoreAuthTokenProvider(dataStore)
}
