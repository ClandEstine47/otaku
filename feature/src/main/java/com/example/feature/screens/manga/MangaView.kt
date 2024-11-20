package com.example.feature.screens.manga

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.common.ErrorScreen
import com.example.feature.common.InfiniteHorizontalPager
import com.example.feature.common.MediaItem
import com.example.feature.common.SearchBar
import com.example.feature.common.TitleWithExpandButton
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

@Composable
fun MangaView(
    navActionManager: NavActionManager,
    hazeState: HazeState,
    mangaViewModel: MangaViewModel = hiltViewModel(),
) {
    val uiState by mangaViewModel.state.collectAsStateWithLifecycle()

    /**
     * Haze blur effect working only for API 32+
     */
    Column(
        modifier =
            Modifier
                .haze(
                    hazeState,
                    HazeStyle(
                        tint = MaterialTheme.colorScheme.background.copy(alpha = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) .2f else .8f),
                        blurRadius = 30.dp,
                        noiseFactor = HazeDefaults.noiseFactor,
                    ),
                )
                .fillMaxSize()
                .absolutePadding()
                .verticalScroll(rememberScrollState()),
    ) {
        if (uiState.isLoading) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (uiState.error == null) {
                MangaContent(
                    navActionManager = navActionManager,
                    trendingMangaList = uiState.trendingMangaList,
                    popularMangaList = uiState.popularMangaList,
                    popularManhwaList = uiState.popularManhwaList,
                    popularNovelList = uiState.popularNovelList,
                    popularOneShotList = uiState.popularOneShotList,
                )
            } else {
                ErrorScreen(
                    onRetryClick = {
                        mangaViewModel.loadData()
                    },
                )
            }
        }
    }
}

@Composable
fun MangaContent(
    navActionManager: NavActionManager,
    trendingMangaList: List<Media>? = null,
    popularMangaList: List<Media>? = null,
    popularManhwaList: List<Media>? = null,
    popularNovelList: List<Media>? = null,
    popularOneShotList: List<Media>? = null,
) {
    val mediaType = MediaType.MANGA

    if (trendingMangaList != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            InfiniteHorizontalPager(
                mediaList = trendingMangaList,
                onBannerItemClick = { mediaId ->
                    navActionManager.toMediaDetail(
                        id = mediaId,
                        mediaType = mediaType,
                    )
                },
            )

            SearchBar(
                mediaType = mediaType,
                onSearchBarClick = {
                    navActionManager.toMediaSearch(mediaType = mediaType)
                },
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))

    popularMangaList?.let { mangas ->
        TitleWithExpandButton(
            titleId = R.string.popular_manga,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_manga,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_MANGA,
                )
            },
        )

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(mangas) { manga ->
                MediaItem(
                    media = manga,
                    isAnime = false,
                    onClick = { id ->
                        navActionManager.toMediaDetail(
                            id = id,
                            mediaType = mediaType,
                        )
                    },
                )
            }
        }
    }

    popularManhwaList?.let { manhwas ->
        TitleWithExpandButton(
            titleId = R.string.popular_manhwa,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_manhwa,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_MANHWA,
                )
            },
        )

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(manhwas) { manhwa ->
                MediaItem(
                    media = manhwa,
                    isAnime = false,
                    onClick = { id ->
                        navActionManager.toMediaDetail(
                            id = id,
                            mediaType = mediaType,
                        )
                    },
                )
            }
        }
    }

    popularNovelList?.let { novels ->
        TitleWithExpandButton(
            titleId = R.string.popular_manhwa,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_novel,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_NOVEL,
                )
            },
        )

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(novels) { novel ->
                MediaItem(
                    media = novel,
                    isAnime = false,
                    onClick = { id ->
                        navActionManager.toMediaDetail(
                            id = id,
                            mediaType = mediaType,
                        )
                    },
                )
            }
        }
    }

    popularOneShotList?.let { oneShorts ->
        TitleWithExpandButton(
            titleId = R.string.popular_one_shot,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_one_shot,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.ONE_SHOT,
                )
            },
        )

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(oneShorts) { oneShort ->
                MediaItem(
                    media = oneShort,
                    isAnime = false,
                    onClick = { id ->
                        navActionManager.toMediaDetail(
                            id = id,
                            mediaType = mediaType,
                        )
                    },
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(100.dp))
}
