package com.example.feature.mediadetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.recommendation.RecommendationConnection
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.anime.ExpandMediaListButton
import com.example.feature.anime.ImageCard
import com.example.feature.anime.OtakuImageCardTitle
import com.example.feature.anime.OtakuTitle

@Composable
fun MediaRecommendations(
    mediaRecommendation: RecommendationConnection,
    navActionManager: NavActionManager,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(id = R.string.recommendations)
        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                // todo: navigate to MediaListView
            },
        )
    }

    LazyRow(
        modifier =
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        mediaRecommendation.nodes?.let { medias ->
            items(medias) { media ->
                val painter =
                    rememberAsyncImagePainter(
                        model = media.mediaRecommendation?.coverImage?.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                    Modifier
                        .clickable {
                            media.mediaRecommendation?.type?.let { type ->
                                media.mediaRecommendation?.idAniList?.let { id ->
                                    navActionManager.toMediaDetail(
                                        id = id,
                                        mediaType = type,
                                    )
                                }
                            }
                        },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (media.mediaRecommendation?.meanScore?.toDouble())?.div(10) ?: 0.0,
                        isAnime = media.mediaRecommendation?.type == MediaType.ANIME,
                        totalChapters = media.mediaRecommendation?.chapters,
                        totalEpisodes = media.mediaRecommendation?.episodes,
                        releasedEpisodes = media.mediaRecommendation?.nextAiringEpisode?.episode?.minus(
                            1
                        ),
                        format = media.mediaRecommendation?.format?.name,
                    )

                    OtakuImageCardTitle(title = media.mediaRecommendation?.title?.english?.ifBlank { media.mediaRecommendation?.title?.romaji }
                        ?: "")
                }
            }
        }
    }
}