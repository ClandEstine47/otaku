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
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.core.navigation.StartDestination
import com.example.core.navigation.navigateAndReplaceStartRoute
import com.example.feature.screens.BottomNavBar
import com.example.feature.screens.NavDestination
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
                OtakuScreen.HomeTab.toString(),
                OtakuScreen.MangaTab.toString(),
            )

        val destinationToTab =
            mapOf(
                OtakuScreen.AnimeTab.toString() to 0,
                OtakuScreen.HomeTab.toString() to 1,
                OtakuScreen.MangaTab.toString() to 2,
            )

        // By default, set index to 1 (Home)
        LaunchedEffect(Unit) {
            bottomTabIndex = destinationToTab[startDestination] ?: 1
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
                                        scope.launch {
                                            dataStore.saveRoute(screen.toString())
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
