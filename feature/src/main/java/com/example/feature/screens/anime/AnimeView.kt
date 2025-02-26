package com.example.feature.screens.anime

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.data.service.isOnline
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.airing.AiringSchedule
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
fun AnimeView(
    navActionManager: NavActionManager,
    hazeState: HazeState,
    animeViewModel: AnimeViewModel = hiltViewModel(),
) {
    val uiState by animeViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isOnline by remember {
        mutableStateOf(isOnline(context))
    }

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
        if (isOnline) {
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
                    AnimeContent(
                        navActionManager = navActionManager,
                        trendingNowMedia = uiState.trendingNowMedia,
                        recentlyUpdatedMedia = uiState.recentlyUpdatedMedia,
                        currentSeasonMedia = uiState.currentSeasonMedia,
                        popularNowMedia = uiState.popularMedia,
                        nextSeasonMedia = uiState.nextSeasonMedia,
                    )
                } else {
                    ErrorScreen(
                        modifier = Modifier.padding(top = 350.dp),
                        onRetryClick = {
                            isOnline = isOnline(context)
                            animeViewModel.loadData()
                        },
                    )
                }
            }
        } else {
            ErrorScreen(
                modifier = Modifier.padding(top = 350.dp),
                errorMessage = stringResource(R.string.no_internet),
                onRetryClick = {
                    isOnline = isOnline(context)
                    animeViewModel.loadData()
                },
            )
        }
    }
}

@Composable
fun AnimeContent(
    navActionManager: NavActionManager,
    trendingNowMedia: List<Media>? = null,
    recentlyUpdatedMedia: List<AiringSchedule>? = null,
    currentSeasonMedia: List<Media>? = null,
    popularNowMedia: List<Media>? = null,
    nextSeasonMedia: List<Media>? = null,
) {
    val mediaType = MediaType.ANIME

    if (trendingNowMedia != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            InfiniteHorizontalPager(
                mediaList = trendingNowMedia,
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

    recentlyUpdatedMedia?.let { recentlyUpdatedAnime ->
        TitleWithExpandButton(
            titleId = R.string.recently_updated,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.calendar,
                    mediaType = MediaType.ANIME,
                    contentType = MediaListContentType.RECENTLY_UPDATED,
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
            items(recentlyUpdatedAnime) { anime ->
                MediaItem(
                    media = anime.media,
                    isAnime = true,
                    releasedEpisodes = anime.episode,
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

    currentSeasonMedia?.let { currentSeasonAnime ->
        TitleWithExpandButton(
            titleId = R.string.current_season,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.current_season,
                    mediaType = MediaType.ANIME,
                    contentType = MediaListContentType.CURRENT_SEASON,
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
            items(currentSeasonAnime) { anime ->
                MediaItem(
                    media = anime,
                    isAnime = true,
                    releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
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

    popularNowMedia?.let { popularAnime ->
        TitleWithExpandButton(
            titleId = R.string.popular_now,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_now,
                    mediaType = MediaType.ANIME,
                    contentType = MediaListContentType.POPULAR_NOW,
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
            items(popularAnime) { anime ->
                MediaItem(
                    media = anime,
                    isAnime = true,
                    releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
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

    nextSeasonMedia?.let { nextSeasonAnime ->
        TitleWithExpandButton(
            titleId = R.string.next_season,
            onExpandClick = {
                navActionManager.toMediaList(
                    titleId = R.string.next_season,
                    mediaType = MediaType.ANIME,
                    contentType = MediaListContentType.NEXT_SEASON,
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
            items(nextSeasonAnime) { anime ->
                MediaItem(
                    media = anime,
                    isAnime = true,
                    showScore = false,
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
