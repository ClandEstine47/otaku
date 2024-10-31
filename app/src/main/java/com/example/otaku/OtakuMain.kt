package com.example.otaku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.core.navigation.StartDestination
import com.example.core.navigation.navigateAndReplaceStartRoute
import com.example.feature.screens.BottomNavBar
import com.example.otaku.ui.theme.OtakuTheme
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch

@Composable
fun OtakuMain() {
    OtakuTheme {
        val navController = rememberNavController()
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)
        val hazeState = remember { HazeState() }
        val context = LocalContext.current
        val dataStore = StartDestination(context)
        val startDestination =
            remember {
                dataStore.getInitialRoute()
            }
        var bottomTabIndex by rememberSaveable {
            mutableStateOf<Int?>(null)
        }

        var showBottomBar by rememberSaveable {
            mutableStateOf(true)
        }

        val bottomNavBarRoutes =
            listOf(
                OtakuScreen.AnimeTab.toString(),
                OtakuScreen.MangaTab.toString(),
            )

        LaunchedEffect(Unit) {
            bottomTabIndex = if (startDestination == OtakuScreen.AnimeTab.toString()) 0 else 1
        }

        LaunchedEffect(navBackStackEntry) {
            val currentRoute = navBackStackEntry?.destination.toString().substringAfterLast('.')

            showBottomBar = bottomNavBarRoutes.contains(currentRoute)
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        bottomTabIndex?.let { index ->
                            BottomNavBar(
                                hazeState = hazeState,
                                tabIndex = index,
                                navigate = { mediaType ->
                                    when (mediaType) {
                                        MediaType.ANIME -> {
                                            bottomTabIndex = 0
                                            navController.navigateAndReplaceStartRoute(OtakuScreen.AnimeTab)
                                            scope.launch {
                                                dataStore.saveRoute(OtakuScreen.AnimeTab.toString())
                                            }
                                        }
                                        MediaType.MANGA -> {
                                            bottomTabIndex = 1
                                            navController.navigateAndReplaceStartRoute(OtakuScreen.MangaTab)
                                            scope.launch {
                                                dataStore.saveRoute(OtakuScreen.MangaTab.toString())
                                            }
                                        }
                                    }
                                },
                            )
                        }
                    }
                },
            ) { padding ->
                MainNavigation(
                    navController = navController,
                    navActionManager = navActionManager,
                    startDestination = startDestination,
                    padding = padding,
                    hazeState = hazeState,
                )
            }
        }
    }
}
