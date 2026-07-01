package com.example.otaku

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.manager.AppUpdateManager
import com.example.feature.Utils.firstBlocking
import com.example.otaku.manager.PlayStoreUpdateManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @SuppressLint("ContextCastToActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        intent?.data?.let { viewModel.onIntentDataReceived(it) }

        val initialIsLoggedIn = viewModel.isLoggedIn.firstBlocking()
        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialIsLoggedIn)
            val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
            val activity = LocalContext.current as Activity

            OtakuMain(
                isLoggedIn = isLoggedIn,
                themeSettings = themeSettings,
                appUpdateManager = appUpdateManager,
                startUpdateFlow = { appUpdateManager.startFlexibleUpdate(activity) },
            )
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
