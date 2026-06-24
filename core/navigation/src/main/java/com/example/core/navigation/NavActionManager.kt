package com.example.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.media.MediaType

@Immutable
class NavActionManager(
    private val navController: NavHostController,
) {
    fun navigateBack() {
        navController.navigateUp()
    }

    fun toMediaSearch(
        mediaType: MediaType,
    ) {
        navController.navigate(
            OtakuScreen.MediaSearch(
                mediaType = mediaType,
            ),
        )
    }

    fun toMediaList(
        titleId: Int,
        mediaId: Int? = null,
        mediaType: MediaType,
        contentType: MediaListContentType,
        userId: Int? = null,
        showStatusTabs: Boolean = false,
    ) {
        navController.navigate(
            OtakuScreen.MediaList(
                titleId = titleId,
                mediaId = mediaId,
                mediaType = mediaType,
                contentType = contentType,
                userId = userId,
                showStatusTabs = showStatusTabs,
            ),
        )
    }

    fun toMediaDetail(
        id: Int,
        mediaType: MediaType,
    ) {
        navController.navigate(
            OtakuScreen.MediaDetail(
                id = id,
                mediaType = mediaType,
            ),
        )
    }

    fun toUserCurrentAnimeList(
        titleId: Int,
        userId: Int?,
        showStatusTabs: Boolean = false,
    ) {
        toMediaList(
            titleId = titleId,
            mediaType = MediaType.ANIME,
            contentType = MediaListContentType.USER_CURRENT_ANIME,
            userId = userId,
            showStatusTabs = showStatusTabs,
        )
    }

    fun toUserCurrentMangaList(
        titleId: Int,
        userId: Int?,
        showStatusTabs: Boolean = false,
    ) {
        toMediaList(
            titleId = titleId,
            mediaType = MediaType.MANGA,
            contentType = MediaListContentType.USER_CURRENT_MANGA,
            userId = userId,
            showStatusTabs = showStatusTabs,
        )
    }

    fun toNotifications() {
        navController.navigate(OtakuScreen.Notifications)
    }

    fun toProfile(userId: Int? = null) {
        navController.navigate(
            OtakuScreen.Profile(
                userId = userId,
            ),
        )
    }

    fun toSettings() {
        navController.navigate(OtakuScreen.Settings)
    }

    fun toTheme() {
        navController.navigate(OtakuScreen.Theme)
    }

    fun toAbout() {
        navController.navigate(OtakuScreen.About)
    }

    companion object {
        @Composable
        fun rememberNavActionManager(navController: NavHostController = rememberNavController()) =
            remember {
                NavActionManager(navController)
            }
    }
}
