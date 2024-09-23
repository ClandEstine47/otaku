package com.example.feature.mediadetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaConnection
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.anime.ImageCard
import com.example.feature.anime.OtakuImageCardTitle
import com.example.feature.anime.OtakuTitle

@Composable
fun MediaRelations(
    mediaConnection: MediaConnection,
    navActionManager: NavActionManager,
) {
    OtakuTitle(id = R.string.relations)

    LazyRow(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        mediaConnection.edges?.let { medias ->
            items(medias) { media ->
                val painter =
                    rememberAsyncImagePainter(
                        model = media.node.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .clickable {
                                media.node.type?.let { type ->
                                    navActionManager.toMediaDetail(
                                        id = media.node.idAniList,
                                        mediaType = type,
                                    )
                                }
                            },
                ) {
                    ImageCard(
                        painter = painter,
                        score = (media.node.meanScore.toDouble()) / 10,
                        isAnime = media.node.type == MediaType.ANIME,
                        totalChapters = media.node.chapters,
                        totalEpisodes = media.node.episodes,
                        releasedEpisodes = media.node.nextAiringEpisode?.episode?.minus(1),
                        format = media.node.format?.name,
                    )

                    OtakuImageCardTitle(title = media.node.title.english.ifBlank { media.node.title.romaji })

                    Spacer(modifier = Modifier.height(3.dp))

                    media.relationType?.stringRes?.let { relationType ->
                        OtakuTitle(
                            title = stringResource(id = relationType),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
