package com.example.otaku

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.core.domain.manager.AppUpdateManager
import com.example.feature.Utils.firstBlocking
import com.example.otaku.manager.PlayStoreUpdateManager
import com.example.otaku.notifications.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                if (viewModel.isLoggedIn.firstBlocking() && viewModel.appSettingsFlow.firstBlocking().notificationsEnabled) {
                    NotificationWorker.schedule(this)
                }
            }
        }

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent?.data?.let { viewModel.onIntentDataReceived(it) }

        val initialIsLoggedIn = viewModel.isLoggedIn.firstBlocking()
        val initialNotificationsEnabled = viewModel.appSettingsFlow.firstBlocking().notificationsEnabled

        // Request runtime notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (initialIsLoggedIn && initialNotificationsEnabled) {
                NotificationWorker.schedule(this)
            }
        } else if (initialIsLoggedIn && initialNotificationsEnabled) {
            NotificationWorker.schedule(this)
        }

        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialIsLoggedIn)
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
            val activity = LocalContext.current as Activity

            OtakuMain(
                isLoggedIn = isLoggedIn,
                appSettings = appSettings,
                appUpdateManager = appUpdateManager,
                startUpdateFlow = { appUpdateManager.startFlexibleUpdate(activity) },
            )
        }

        // Observe login state and settings to schedule/cancel notification worker dynamically
        lifecycleScope.launch {
            combine(viewModel.isLoggedIn, viewModel.appSettingsFlow) { loggedIn, settings ->
                loggedIn to settings.notificationsEnabled
            }.collect { (loggedIn, notificationsEnabled) ->
                updateNotificationWorker(loggedIn, notificationsEnabled)
            }
        }
    }

    private fun updateNotificationWorker(
        isLoggedIn: Boolean,
        notificationsEnabled: Boolean,
    ) {
        if (isLoggedIn && notificationsEnabled) {
            val hasPermission =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (hasPermission) NotificationWorker.schedule(this)
        } else {
            NotificationWorker.cancel(this)
        }
    }

    override fun onStart() {
        super.onStart()
        appUpdateManager.checkForUpdates()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onIntentDataReceived(intent.data)
    }

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.dispose()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PlayStoreUpdateManager.FLEXIBLE_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Timber.e("Update flow failed! Result code: $resultCode")
            }
        }
    }
}
