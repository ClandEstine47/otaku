package com.example.core.domain.manager

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable

interface AppUpdateManager {
    val updateStatus: StateFlow<UpdateStatus>

    fun checkForUpdates()

    fun startFlexibleUpdate(activity: Activity)

    fun completeUpdate()

    fun dispose()

    sealed class UpdateStatus : Serializable {
        data object Idle : UpdateStatus()

        data object UpdateAvailable : UpdateStatus()

        data class Downloading(
            val bytesDownloaded: Long,
            val totalBytesToDownload: Long,
        ) : UpdateStatus()

        data object Downloaded : UpdateStatus()

        data class Failed(
            val throwable: Throwable? = null,
        ) : UpdateStatus()
    }
}
