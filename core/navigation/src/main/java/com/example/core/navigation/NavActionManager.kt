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
        mediaType: MediaType,
        contentType: MediaListContentType,
    ) {
        navController.navigate(
            OtakuScreen.MediaList(
                titleId = titleId,
                mediaType = mediaType,
                contentType = contentType,
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

    companion object {
        @Composable
        fun rememberNavActionManager(navController: NavHostController = rememberNavController()) =
            remember {
                NavActionManager(navController)
            }
    }
}
