package com.example.otaku

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.CustomNavType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.screens.anime.AnimeView
import com.example.feature.screens.anime.AnimeViewModel
import com.example.feature.screens.home.HomeView
import com.example.feature.screens.home.HomeViewModel
import com.example.feature.screens.manga.MangaView
import com.example.feature.screens.manga.MangaViewModel
import com.example.feature.screens.mediadetail.MediaDetailView
import com.example.feature.screens.medialist.MediaListView
import com.example.feature.screens.notifications.NotificationsView
import com.example.feature.screens.profile.ProfileView
import com.example.feature.screens.search.MediaSearchView
import com.example.feature.screens.settings.AboutView
import com.example.feature.screens.settings.SettingsView
import com.example.feature.screens.settings.ThemeView
import dev.chrisbanes.haze.HazeState
import kotlin.reflect.typeOf

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    isLoggedIn: Boolean,
    padding: PaddingValues,
    hazeState: HazeState,
    animeViewModel: AnimeViewModel,
    homeViewModel: HomeViewModel,
    mangaViewModel: MangaViewModel,
) {
    val initialDestination = OtakuScreen.HomeTab

    NavHost(
        navController = navController,
        startDestination = initialDestination,
        modifier =
        Modifier,
    ) {
        composable<OtakuScreen.AnimeTab> {
            AnimeView(
                navActionManager = navActionManager,
                hazeState = hazeState,
                animeViewModel = animeViewModel,
            )
        }

        composable<OtakuScreen.MangaTab> {
            MangaView(
                navActionManager = navActionManager,
                hazeState = hazeState,
                mangaViewModel = mangaViewModel,
            )
        }

        composable<OtakuScreen.HomeTab> {
            HomeView(
                navActionManager = navActionManager,
                isLoggedIn = isLoggedIn,
                hazeState = hazeState,
                homeViewModel = homeViewModel,
            )
        }

        composable<OtakuScreen.MediaSearch>(
            typeMap =
                mapOf(
                    typeOf<MediaType>() to CustomNavType(MediaType::class.java, MediaType.serializer()),
                ),
        ) {
            MediaSearchView(
                arguments = it.toRoute(),
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.MediaList>(
            typeMap =
                mapOf(
                    typeOf<MediaType>() to CustomNavType(MediaType::class.java, MediaType.serializer()),
                    typeOf<MediaListContentType>() to CustomNavType(MediaListContentType::class.java, MediaListContentType.serializer()),
                ),
        ) {
            MediaListView(
                arguments = it.toRoute(),
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.MediaDetail>(
            typeMap =
                mapOf(
                    typeOf<MediaType>() to CustomNavType(MediaType::class.java, MediaType.serializer()),
                ),
        ) {
            MediaDetailView(
                arguments = it.toRoute(),
                isLoggedIn = isLoggedIn,
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.Notifications> {
            NotificationsView(
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.Profile> {
            ProfileView(
                arguments = it.toRoute(),
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.Settings> {
            SettingsView(
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.Theme> {
            ThemeView(
                navActionManager = navActionManager,
            )
        }

        composable<OtakuScreen.About> {
            AboutView(
                navActionManager = navActionManager,
            )
        }
    }
}
