package com.example.otaku

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.core.navigation.DeepLink
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.anime.AnimeView

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    deepLink: DeepLink?,
) {
    NavHost(
        navController = navController,
        startDestination = OtakuScreen.AnimeTab,
        modifier = Modifier.padding(8.dp),
    ) {
        composable<OtakuScreen.AnimeTab> {
            AnimeView(navActionManager = navActionManager)
        }
    }
}
