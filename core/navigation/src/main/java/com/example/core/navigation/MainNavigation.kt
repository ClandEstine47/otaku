package com.example.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
            AnimeView()
        }
    }
}
