package com.example.otaku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.BottomNavBar
import com.example.feature.NavBarItem
import com.example.otaku.ui.theme.OtakuTheme

@Composable
fun OtakuMain() {
    OtakuTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val navActionManager = NavActionManager.rememberNavActionManager(navController)

        var showBottomBar by rememberSaveable {
            mutableStateOf(true)
        }

        val bottomNavBarRoutes =
            listOf(
                OtakuScreen.AnimeTab.toString(),
                OtakuScreen.MangaTab.toString(),
            )

        LaunchedEffect(navBackStackEntry) {
            val currentRoute = navBackStackEntry?.destination.toString().substringAfterLast('.')

            showBottomBar = bottomNavBarRoutes.contains(currentRoute)
        }

        val navBarItems =
            listOf(
                NavBarItem(title = "Anime", iconEnabled = com.example.feature.R.drawable.anime_enabled, iconDisabled = com.example.feature.R.drawable.anime_disabled),
                NavBarItem(title = "Manga", iconEnabled = com.example.feature.R.drawable.manga_enabled, iconDisabled = com.example.feature.R.drawable.manga_disabled),
            )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavBar(
                            navBarItems = navBarItems,
                            navigate = { title ->
                                if (title == "Anime") {
                                    navController.navigate(OtakuScreen.AnimeTab)
                                } else {
                                    navController.navigate(OtakuScreen.MangaTab)
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
                )
            }
        }
    }
}
