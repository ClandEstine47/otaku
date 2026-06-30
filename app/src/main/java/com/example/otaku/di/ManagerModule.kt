package com.example.otaku.di

import com.example.core.domain.manager.AppUpdateManager
import com.example.otaku.manager.PlayStoreUpdateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ManagerModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindAppUpdateManager(impl: PlayStoreUpdateManager): AppUpdateManager
}
