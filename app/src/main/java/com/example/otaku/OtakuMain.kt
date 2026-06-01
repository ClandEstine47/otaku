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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
) {
    OtakuTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)
        val hazeState = remember { HazeState() }
        val animeViewModel: AnimeViewModel = hiltViewModel()
        val homeViewModel: HomeViewModel = hiltViewModel()
        val mangaViewModel: MangaViewModel = hiltViewModel()
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

        // By default, set index to 1 (Home)
        LaunchedEffect(Unit) {
            bottomTabIndex = 1
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
