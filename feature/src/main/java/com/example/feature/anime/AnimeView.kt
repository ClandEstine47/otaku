package com.example.feature.anime

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.common.InfiniteHorizontalPager
import com.example.feature.common.SearchBar
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
            AnimeContent(
                navActionManager = navActionManager,
                trendingNowMedia = uiState.trendingNowMedia,
                recentlyUpdatedMedia = uiState.recentlyUpdatedMedia,
                currentSeasonMedia = uiState.currentSeasonMedia,
                popularNowMedia = uiState.popularMedia,
                nextSeasonMedia = uiState.nextSeasonMedia,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OtakuTitle(
                id = R.string.recently_updated,
                modifier = Modifier.padding(start = 10.dp),
            )

            ExpandMediaListButton(
                modifier = Modifier,
                onButtonClick = {
                    navActionManager.toMediaList(
                        titleId = R.string.calendar,
                        mediaType = MediaType.ANIME,
                        contentType = MediaListContentType.RECENTLY_UPDATED,
                    )
                },
            )
        }

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(recentlyUpdatedAnime) { anime ->
                val painter =
                    rememberAsyncImagePainter(
                        model = anime.media.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier.clickable {
                            navActionManager.toMediaDetail(
                                id = anime.media.idAniList,
                                mediaType = mediaType,
                            )
                        },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (anime.media.meanScore.toDouble()) / 10,
                        isAnime = true,
                        totalEpisodes = anime.media.episodes,
                        releasedEpisodes = anime.episode,
                        format = anime.media.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.media.title.english.ifBlank { anime.media.title.romaji })
                }
            }
        }
    }

    currentSeasonMedia?.let { currentSeasonAnime ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OtakuTitle(
                id = R.string.current_season,
                modifier = Modifier.padding(start = 10.dp),
            )

            ExpandMediaListButton(
                modifier = Modifier,
                onButtonClick = {
                    navActionManager.toMediaList(
                        titleId = R.string.current_season,
                        mediaType = MediaType.ANIME,
                        contentType = MediaListContentType.CURRENT_SEASON,
                    )
                },
            )
        }

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(currentSeasonAnime) { anime ->
                val painter =
                    rememberAsyncImagePainter(
                        model = anime.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .clickable {
                                navActionManager.toMediaDetail(
                                    id = anime.idAniList,
                                    mediaType = mediaType,
                                )
                            },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (anime.meanScore.toDouble()) / 10,
                        isAnime = true,
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.english.ifBlank { anime.title.romaji })
                }
            }
        }
    }

    popularNowMedia?.let { popularAnime ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OtakuTitle(
                id = R.string.popular_now,
                modifier = Modifier.padding(start = 10.dp),
            )

            ExpandMediaListButton(
                modifier = Modifier,
                onButtonClick = {
                    navActionManager.toMediaList(
                        titleId = R.string.popular_now,
                        mediaType = MediaType.ANIME,
                        contentType = MediaListContentType.POPULAR_NOW,
                    )
                },
            )
        }

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(popularAnime) { anime ->
                val painter =
                    rememberAsyncImagePainter(
                        model = anime.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .clickable {
                                navActionManager.toMediaDetail(
                                    id = anime.idAniList,
                                    mediaType = mediaType,
                                )
                            },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (anime.meanScore.toDouble()) / 10,
                        isAnime = true,
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.english.ifBlank { anime.title.romaji })
                }
            }
        }
    }

    nextSeasonMedia?.let { nextSeasonAnime ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OtakuTitle(
                id = R.string.next_season,
                modifier = Modifier.padding(start = 10.dp),
            )

            ExpandMediaListButton(
                modifier = Modifier,
                onButtonClick = {
                    navActionManager.toMediaList(
                        titleId = R.string.next_season,
                        mediaType = MediaType.ANIME,
                        contentType = MediaListContentType.NEXT_SEASON,
                    )
                },
            )
        }

        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(nextSeasonAnime) { anime ->
                val painter =
                    rememberAsyncImagePainter(
                        model = anime.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .clickable {
                                navActionManager.toMediaDetail(
                                    id = anime.idAniList,
                                    mediaType = mediaType,
                                )
                            },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (anime.meanScore.toDouble()) / 10,
                        isAnime = true,
                        showScore = false,
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = null,
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.english.ifBlank { anime.title.romaji })
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(100.dp))
}
