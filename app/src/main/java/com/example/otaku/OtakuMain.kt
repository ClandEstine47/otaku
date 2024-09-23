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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.BottomNavBar
import com.example.feature.NavBarItem
import com.example.otaku.ui.theme.OtakuTheme
import dev.chrisbanes.haze.HazeState

@Composable
fun OtakuMain() {
    OtakuTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)
        val hazeState = remember { HazeState() }

        var showBottomBar by rememberSaveable {
            mutableStateOf(true)
        }

        val bottomNavBarRoutes =
            listOf(
                OtakuScreen.AnimeTab.toString(),
                OtakuScreen.MangaTab.toString(),
            )

        val navBarItems =
            listOf(
                NavBarItem(mediaType = MediaType.ANIME, iconEnabled = com.example.feature.R.drawable.anime_enabled, iconDisabled = com.example.feature.R.drawable.anime_disabled),
                NavBarItem(mediaType = MediaType.MANGA, iconEnabled = com.example.feature.R.drawable.manga_enabled, iconDisabled = com.example.feature.R.drawable.manga_disabled),
            )

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
                        BottomNavBar(
                            navBarItems = navBarItems,
                            hazeState = hazeState,
                            navigate = { mediaType ->
                                when (mediaType) {
                                    MediaType.ANIME -> {
                                        navController.navigate(OtakuScreen.AnimeTab)
                                    }
                                    MediaType.MANGA -> {
                                        navController.navigate(OtakuScreen.MangaTab)
                                    }
                                }
                            },
                        )
                    }
                },
            ) { padding ->
                MainNavigation(
                    navController = navController,
                    navActionManager = navActionManager,
                    deepLink = null,
                    padding = padding,
                    hazeState = hazeState,
                )
            }
        }
    }
}
