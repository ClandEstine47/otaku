package com.example.feature.anime

import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun AnimeView(
    navActionManager: NavActionManager,
    animeViewModel: AnimeViewModel = hiltViewModel(),
) {
    val uiState by animeViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
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
    if (trendingNowMedia != null) {
        InfiniteHorizontalPager(mediaList = trendingNowMedia)
    }

    Spacer(modifier = Modifier.height(40.dp))

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
                    titleId = R.string.recently_updated,
                    mediaType = MediaType.ANIME,
                    contentType = MediaListContentType.RECENTLY_UPDATED,
                )
            },
        )
    }

    recentlyUpdatedMedia?.let { recentlyUpdatedAnime ->
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

    currentSeasonMedia?.let { currentSeasonAnime ->
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

    popularNowMedia?.let { popularAnime ->
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

    nextSeasonMedia?.let { nextSeasonAnime ->
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
}
