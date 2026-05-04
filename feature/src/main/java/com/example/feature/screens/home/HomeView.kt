package com.example.feature.screens.home

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.data.service.isOnline
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.common.ErrorScreen
import com.example.feature.common.OtakuTitle
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

@Composable
fun HomeView(
    navActionManager: NavActionManager,
    hazeState: HazeState,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by homeViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isOnline by remember {
        mutableStateOf(isOnline(context))
    }

    // Haze blur effect working only for API 32+
    Column(
        modifier =
            Modifier
                .haze(
                    hazeState,
                    HazeStyle(
                        tint = MaterialTheme.colorScheme.background.copy(alpha = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) .2f else .8f),
                        blurRadius = 30.dp,
                        noiseFactor = HazeDefaults.noiseFactor,
                    ),
                ).fillMaxSize()
                .absolutePadding(),
    ) {
        if (isOnline) {
            if (uiState.isLoading) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (uiState.error == null) {
                    HomeContent()
                } else {
                    ErrorScreen(
                        modifier = Modifier.padding(top = 350.dp),
                        onRetryClick = {
                            isOnline = isOnline(context)
                            // Load data
                        },
                    )
                }
            }
        } else {
            ErrorScreen(
                modifier = Modifier.padding(top = 350.dp),
                errorMessage = stringResource(R.string.no_internet),
                onRetryClick = {
                    isOnline = isOnline(context)
                    // Load data
                },
            )
        }
    }
}

@Composable
fun HomeContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OtakuTitle(
            title = stringResource(R.string.otaku),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 60.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OtakuTitle(
            id = R.string.powered_by_anilist,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(150.dp))

        Button(
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp),
            onClick = {
                // todo: navigate to login page
            },
        ) {
            OtakuTitle(
                id = R.string.login,
                color = MaterialTheme.colorScheme.background,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp),
            onClick = {
                // todo: navigate to register page
            },
        ) {
            OtakuTitle(
                id = R.string.register,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun HomeContentPreview(modifier: Modifier = Modifier) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .absolutePadding(),
    ) {
        HomeContent()
    }
}
