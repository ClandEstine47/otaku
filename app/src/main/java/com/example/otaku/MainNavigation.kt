package com.example.otaku

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.core.navigation.DeepLink
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.anime.AnimeView
import com.example.feature.manga.MangaView

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    deepLink: DeepLink?,
    padding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = OtakuScreen.AnimeTab,
        modifier =
            Modifier
                .padding(bottom = padding.calculateBottomPadding()),
    ) {
        composable<OtakuScreen.AnimeTab> {
            AnimeView(navActionManager = navActionManager)
        }

        composable<OtakuScreen.MangaTab> {
            MangaView(navActionManager = navActionManager)
        }
    }
}
