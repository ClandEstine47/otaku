package com.example.core.data.di

import com.apollographql.apollo3.ApolloClient
import com.example.core.domain.service.MediaService
import com.example.core.network.service.MediaServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideMediaService(apolloClient: ApolloClient): MediaService {
        return MediaServiceImpl(apolloClient)
    }
}
