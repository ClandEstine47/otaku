package com.example.otaku

import androidx.compose.foundation.layout.PaddingValues
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
import com.example.feature.screens.manga.MangaView
import com.example.feature.screens.mediadetail.MediaDetailView
import com.example.feature.screens.medialist.MediaListView
import com.example.feature.screens.search.MediaSearchView
import dev.chrisbanes.haze.HazeState
import kotlin.reflect.typeOf

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    startDestination: String,
    padding: PaddingValues,
    hazeState: HazeState,
) {
    NavHost(
        navController = navController,
        startDestination =
            if (startDestination == OtakuScreen.AnimeTab.toString()) {
                OtakuScreen.AnimeTab
            } else {
                OtakuScreen.MangaTab
            },
        modifier =
        Modifier,
    ) {
        composable<OtakuScreen.AnimeTab> {
            AnimeView(
                navActionManager = navActionManager,
                hazeState = hazeState,
            )
        }

        composable<OtakuScreen.MangaTab> {
            MangaView(
                navActionManager = navActionManager,
                hazeState = hazeState,
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
                navActionManager = navActionManager,
            )
        }
    }
}
