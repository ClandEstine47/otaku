package com.example.otaku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.domain.manager.AppUpdateManager
import com.example.core.domain.model.settings.ThemeSettings
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.core.navigation.navigateAndReplaceStartRoute
import com.example.feature.screens.BottomNavBar
import com.example.feature.screens.NavDestination
import com.example.feature.screens.anime.AnimeViewModel
import com.example.feature.screens.home.HomeViewModel
import com.example.feature.screens.manga.MangaViewModel
import com.example.otaku.ui.theme.OtakuTheme
import dev.chrisbanes.haze.HazeState

@Composable
fun OtakuMain(
    isLoggedIn: Boolean,
    themeSettings: ThemeSettings,
    appUpdateManager: AppUpdateManager,
    startUpdateFlow: () -> Unit,
) {
    OtakuTheme(settings = themeSettings) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)
        val hazeState = remember { HazeState() }
        val animeViewModel: AnimeViewModel = hiltViewModel()
        val homeViewModel: HomeViewModel = hiltViewModel()
        val mangaViewModel: MangaViewModel = hiltViewModel()
        val snackbarHostState = remember { SnackbarHostState() }

        val updateStatus by appUpdateManager.updateStatus.collectAsStateWithLifecycle()

        var lastHandledStatus by rememberSaveable {
            mutableStateOf<AppUpdateManager.UpdateStatus?>(null)
        }

        var snackbarInProgress by remember {
            mutableStateOf(false)
        }

        var bottomTabIndex by rememberSaveable {
            mutableIntStateOf(1)
        }

        var showBottomBar by rememberSaveable {
            mutableStateOf(true)
        }

        val bottomBarScreens =
            remember {
                setOf(
                    OtakuScreen.AnimeTab::class,
                    OtakuScreen.HomeTab::class,
                    OtakuScreen.MangaTab::class,
                )
            }

        LaunchedEffect(navBackStackEntry) {
            showBottomBar = navBackStackEntry?.destination?.let { destination ->
                bottomBarScreens.any { destination.hasRoute(it) }
            } ?: false
        }

        LaunchedEffect(updateStatus) {
            if (updateStatus == lastHandledStatus) return@LaunchedEffect
            if (snackbarInProgress) return@LaunchedEffect

            lastHandledStatus = updateStatus

            when (updateStatus) {
                AppUpdateManager.UpdateStatus.UpdateAvailable -> {
                    snackbarInProgress = true
                    val result =
                        snackbarHostState.showSnackbar(
                            message = "App update available",
                            actionLabel = "Update",
                            duration = SnackbarDuration.Indefinite,
                        )
                    snackbarInProgress = false
                    if (result == SnackbarResult.ActionPerformed) {
                        startUpdateFlow()
                    }
                }

                is AppUpdateManager.UpdateStatus.Downloading -> {
                    // Progress tracked here: status.bytesDownloaded / status.totalBytesToDownload
                }

                AppUpdateManager.UpdateStatus.Downloaded -> {
                    snackbarInProgress = true
                    val result =
                        snackbarHostState.showSnackbar(
                            message = "Update downloaded",
                            actionLabel = "Restart",
                            duration = SnackbarDuration.Indefinite,
                        )
                    snackbarInProgress = false
                    if (result == SnackbarResult.ActionPerformed) {
                        appUpdateManager.completeUpdate()
                    }
                }

                is AppUpdateManager.UpdateStatus.Failed -> {
                    // Errors handled here
                }

                else -> {}
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavBar(
                            hazeState = hazeState,
                            tabIndex = bottomTabIndex,
                            navigate = { navDestination ->
                                val newIndex =
                                    when (navDestination) {
                                        NavDestination.Anime -> 0
                                        NavDestination.Home -> 1
                                        NavDestination.Manga -> 2
                                    }

                                if (bottomTabIndex != newIndex) {
                                    bottomTabIndex = newIndex
                                    val screen =
                                        when (navDestination) {
                                            NavDestination.Anime -> OtakuScreen.AnimeTab
                                            NavDestination.Home -> OtakuScreen.HomeTab
                                            NavDestination.Manga -> OtakuScreen.MangaTab
                                        }
                                    navController.navigateAndReplaceStartRoute(screen)
                                }
                            },
                        )
                    }
                },
            ) { padding ->
                MainNavigation(
                    navController = navController,
                    navActionManager = navActionManager,
                    padding = padding,
                    isLoggedIn = isLoggedIn,
                    hazeState = hazeState,
                    animeViewModel = animeViewModel,
                    homeViewModel = homeViewModel,
                    mangaViewModel = mangaViewModel,
                )
            }
        }
    }
}
