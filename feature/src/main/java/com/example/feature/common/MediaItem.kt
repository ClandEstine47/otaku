package com.example.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.Media

@Composable
fun MediaItem(
    modifier: Modifier = Modifier,
    media: Media,
    isAnime: Boolean,
    releasedEpisodes: Int? = null,
    showScore: Boolean = true,
    onClick: (Int) -> Unit,
) {
    val painter =
        rememberAsyncImagePainter(
            model = media.coverImage.large,
        )

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier =
            modifier.clickable {
                onClick(media.idAniList)
            },
    ) {
        ImageCard(
            painter = painter,
            score = (media.meanScore.toDouble()) / 10,
            isAnime = isAnime,
            totalEpisodes = media.episodes,
            releasedEpisodes = releasedEpisodes,
            showScore = showScore,
            format = media.format?.name,
        )

        OtakuImageCardTitle(title = media.title.english.ifBlank { media.title.romaji })
    }
}
