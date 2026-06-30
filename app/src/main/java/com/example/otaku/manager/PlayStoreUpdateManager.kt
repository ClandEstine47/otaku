package com.example.otaku.manager

import android.app.Activity
import android.content.Context
import com.example.core.domain.manager.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

class PlayStoreUpdateManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AppUpdateManager {
        private val appUpdateManager = AppUpdateManagerFactory.create(context)
        private var appUpdateInfo: AppUpdateInfo? = null

        private val _updateStatus = MutableStateFlow<AppUpdateManager.UpdateStatus>(AppUpdateManager.UpdateStatus.Idle)
        override val updateStatus: StateFlow<AppUpdateManager.UpdateStatus> = _updateStatus.asStateFlow()

        private val installStateUpdatedListener =
            InstallStateUpdatedListener { state ->
                when (state.installStatus()) {
                    InstallStatus.DOWNLOADING -> {
                        val bytesDownloaded = state.bytesDownloaded()
                        val totalBytes = state.totalBytesToDownload()
                        _updateStatus.value =
                            AppUpdateManager.UpdateStatus.Downloading(
                                bytesDownloaded = bytesDownloaded,
                                totalBytesToDownload = if (totalBytes == 0L) -1L else totalBytes,
                            )
                    }

                    InstallStatus.DOWNLOADED -> {
                        _updateStatus.value = AppUpdateManager.UpdateStatus.Downloaded
                    }

                    InstallStatus.FAILED -> {
                        _updateStatus.value = AppUpdateManager.UpdateStatus.Failed()
                    }
                }
            }

        private var isListenerRegistered = false

        override fun checkForUpdates() {
            if (_updateStatus.value != AppUpdateManager.UpdateStatus.Idle) {
                if (_updateStatus.value is AppUpdateManager.UpdateStatus.Failed) {
                    _updateStatus.value = AppUpdateManager.UpdateStatus.Idle
                } else {
                    return
                }
            }

            appUpdateManager.appUpdateInfo
                .addOnSuccessListener { info ->
                    appUpdateInfo = info

                    val availability = info.updateAvailability()
                    val installStatus = info.installStatus()

                    when {
                        installStatus == InstallStatus.DOWNLOADED -> {
                            _updateStatus.value = AppUpdateManager.UpdateStatus.Downloaded
                        }

                        availability == UpdateAvailability.UPDATE_AVAILABLE && info.isFlexibleUpdateAllowed -> {
                            _updateStatus.value = AppUpdateManager.UpdateStatus.UpdateAvailable
                        }

                        availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                            ensureListenerRegistered()
                        }
                    }
                }.addOnFailureListener {
                    Timber.e(it, "Failed to check for updates")
                    _updateStatus.value = AppUpdateManager.UpdateStatus.Failed(it)
                }
        }

        override fun startFlexibleUpdate(activity: Activity) {
            val info = appUpdateInfo ?: return
            ensureListenerRegistered()

            try {
                @Suppress("DEPRECATION")
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    FLEXIBLE_UPDATE_REQUEST_CODE,
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to start flexible update")
                _updateStatus.value = AppUpdateManager.UpdateStatus.Failed(e)
            }
        }

        override fun completeUpdate() {
            appUpdateManager.completeUpdate()
        }

        override fun dispose() {
            if (isListenerRegistered) {
                appUpdateManager.unregisterListener(installStateUpdatedListener)
                isListenerRegistered = false
            }
        }

        private fun ensureListenerRegistered() {
            if (!isListenerRegistered) {
                appUpdateManager.registerListener(installStateUpdatedListener)
                isListenerRegistered = true
            }
        }

        companion object {
            const val FLEXIBLE_UPDATE_REQUEST_CODE = 1001
        }
    }
