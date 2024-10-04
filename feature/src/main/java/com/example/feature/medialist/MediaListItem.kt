package com.example.feature.medialist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.core.domain.model.airing.AiringSchedule
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaTitle
import com.example.core.domain.model.media.MediaType
import com.example.feature.anime.ImageCard
import com.example.feature.anime.OtakuTitle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaListItem(
    modifier: Modifier = Modifier,
    mediaItem: Media,
    onClick: (id: Int, mediaType: MediaType) -> Unit,
) {
    val title = mediaItem.title.english.ifBlank { mediaItem.title.romaji }
    val coverImage =
        rememberAsyncImagePainter(
            model = mediaItem.coverImage.large,
        )
    val bannerImage =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(
                        mediaItem.bannerImage.ifBlank {
                            mediaItem.coverImage.extraLarge
                        },
                    )
                    .crossfade(true)
                    .build(),
        )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    mediaItem.type?.let { type -> onClick(mediaItem.idAniList, type) }
                },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = bannerImage,
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

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            ImageCard(
                painter = coverImage,
                showBottomBar = true,
                score = mediaItem.meanScore.toDouble() / 10,
                isAnime = mediaItem.type == MediaType.ANIME,
                totalChapters = mediaItem.chapters,
                totalEpisodes = mediaItem.episodes,
                releasedEpisodes = mediaItem.nextAiringEpisode?.episode?.minus(1),
                format = mediaItem.format?.name,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                OtakuTitle(title = title, color = MaterialTheme.colorScheme.onBackground)

                FlowRow(
                    maxItemsInEachRow = Int.MAX_VALUE,
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    mediaItem.genres?.let { genres ->
                        val genreCount = genres.size
                        genres.forEachIndexed { index, genre ->
                            genre?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                )
                                if (index < genreCount - 1) {
                                    Text(
                                        text = " \u00B7 ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaListItem(
    modifier: Modifier = Modifier,
    mediaItem: AiringSchedule,
    onClick: (id: Int, mediaType: MediaType) -> Unit,
) {
    val title = mediaItem.media.title.english.ifBlank { mediaItem.media.title.romaji }
    val coverImage =
        rememberAsyncImagePainter(
            model = mediaItem.media.coverImage.large,
        )
    val bannerImage =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(
                        mediaItem.media.bannerImage.ifBlank {
                            mediaItem.media.coverImage.extraLarge
                        },
                    )
                    .crossfade(true)
                    .build(),
        )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    mediaItem.media.type?.let { type -> onClick(mediaItem.media.idAniList, type) }
                },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = bannerImage,
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

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            ImageCard(
                painter = coverImage,
                showBottomBar = true,
                score = mediaItem.media.meanScore.toDouble() / 10,
                isAnime = mediaItem.media.type == MediaType.ANIME,
                totalChapters = mediaItem.media.chapters,
                totalEpisodes = mediaItem.media.episodes,
                releasedEpisodes = mediaItem.episode,
                format = mediaItem.media.format?.name,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                OtakuTitle(title = title, color = MaterialTheme.colorScheme.onBackground)

                FlowRow(
                    maxItemsInEachRow = Int.MAX_VALUE,
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    mediaItem.media.genres?.let { genres ->
                        val genreCount = genres.size
                        genres.forEachIndexed { index, genre ->
                            genre?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                )
                                if (index < genreCount - 1) {
                                    Text(
                                        text = " \u00B7 ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MediaListItemPreview(modifier: Modifier = Modifier) {
    val fakeMedia =
        Media(
            title =
                MediaTitle(
                    romaji = "Kimetsu no Yaiba",
                    english = "Demon Slayer",
                ),
            format = MediaFormat.TV,
            episodes = 12,
            genres = listOf("Drama", "Mystery", "Action", "Fantasy", "Slice of Life", "Comedy"),
        )
    MediaListItem(mediaItem = fakeMedia, onClick = { _, _ -> })
}
