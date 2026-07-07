package com.example.feature.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.Utils.openActionView
import com.example.feature.Utils.showToast
import com.example.feature.common.BackButton
import com.example.feature.common.OtakuTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutView(
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    val githubUrl = stringResource(R.string.github_url)
    val telegramUrl = stringResource(R.string.telegram_url)
    val futureUpdateMessage = stringResource(R.string.future_update)

    val packageInfo =
        remember(context) {
            try {
                context.packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: Exception) {
                null
            }
        }
    val versionName = packageInfo?.versionName ?: "Unknown"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OtakuTitle(
                        title = stringResource(R.string.about),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    BackButton(
                        onButtonClick = {
                            navActionManager.navigateBack()
                        },
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            OtakuTitle(
                title = stringResource(R.string.otaku),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
            )

            OtakuTitle(
                title = "Version $versionName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(60.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SettingsItem(
                    iconRes = R.drawable.github,
                    titleRes = R.string.github,
                    subtitleRes = R.string.github_subtitle,
                    onClick = {
                        context.openActionView(githubUrl)
                    },
                )

                SettingsItem(
                    iconRes = R.drawable.telegram,
                    titleRes = R.string.telegram,
                    subtitleRes = R.string.telegram_subtitle,
                    onClick = {
                        context.openActionView(telegramUrl)
                    },
                )

                SettingsItem(
                    iconRes = R.drawable.privacy_policy,
                    titleRes = R.string.privacy_policy,
                    subtitleRes = R.string.privacy_policy_subtitle,
                    onClick = {
                        context.showToast(futureUpdateMessage)
                    },
                )
            }
        }
    }
}
