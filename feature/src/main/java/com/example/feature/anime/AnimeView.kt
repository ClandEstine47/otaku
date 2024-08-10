package com.example.feature.anime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
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
                .verticalScroll(rememberScrollState()),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            AnimeContent(
                navActionManager = navActionManager,
                currentSeasonMedia = uiState.currentSeasonMedia,
                recentlyUpdatedMedia = uiState.recentlyUpdatedMedia,
                trendingNowMedia = uiState.trendingNowMedia,
                nextSeasonMedia = uiState.nextSeasonMedia,
                popularNowMedia = uiState.popularMedia,
            )
        }
    }
}

@Composable
fun AnimeContent(
    navActionManager: NavActionManager,
    currentSeasonMedia: List<Media>? = null,
    recentlyUpdatedMedia: List<AiringSchedule>? = null,
    trendingNowMedia: List<Media>? = null,
    nextSeasonMedia: List<Media>? = null,
    popularNowMedia: List<Media>? = null,
) {
    if (currentSeasonMedia != null) {
        InfiniteHorizontalPager(currentSeasonMedia = currentSeasonMedia)
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
                // todo: update later
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
                        totalEpisodes = anime.media.episodes,
                        releasedEpisodes = anime.episode,
                        format = anime.media.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.media.title.romaji)
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
            id = R.string.trending_now,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                // todo: update later
            },
        )
    }

    trendingNowMedia?.let { trendingAnime ->
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(trendingAnime) { anime ->
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
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.romaji)
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
                // todo: update later
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
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.romaji)
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
                // todo: update later
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
                        showScore = false,
                        totalEpisodes = anime.episodes,
                        releasedEpisodes = null,
                        format = anime.format?.name,
                    )

                    OtakuImageCardTitle(title = anime.title.romaji)
                }
            }
        }
    }
}

@Composable
fun AnimatedBannerImage(
    bannerPainter: Painter,
    coverPainter: Painter,
    score: Double,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(464.dp)
                .absolutePadding(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RectangleShape,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = bannerPainter,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .blur(
                            radiusX = 8.dp,
                            radiusY = 8.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded,
                        ),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        MaterialTheme.colorScheme.background,
                                        Color.Transparent,
                                    ),
                                startY = Float.POSITIVE_INFINITY,
                                endY = 0f,
                            ),
                        ),
            )
        }
    }
}

@Composable
fun ExpandMediaListButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onButtonClick()
        },
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = "expand media list",
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun BannerImagePreview() {
    AnimatedBannerImage(
        bannerPainter = painterResource(id = R.drawable.anime_banner_preview),
        coverPainter = painterResource(id = R.drawable.anime_banner_preview),
        score = 5.1,
    )
}
