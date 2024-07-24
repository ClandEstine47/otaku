package com.example.feature.di

import com.example.core.data.repository.MediaRepositoryImpl
import com.example.core.domain.repository.MediaRepository
import com.example.core.domain.service.MediaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeatureModule {

    @Provides
    @Singleton
    fun providesMediaRepository(mediaService: MediaService): MediaRepository {
        return MediaRepositoryImpl(mediaService)
    }
}