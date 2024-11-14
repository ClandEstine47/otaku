package com.example.feature.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.MediaListItem
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager

@Composable
fun MediaGridViewContent(
    navActionManager: NavActionManager,
    mediaList: List<MediaListItem>? = null,
    gridState: LazyGridState = rememberLazyGridState(),
    onLoadMore: () -> Unit,
) {
    gridState.OnBottomReached(
        buffer = 3,
        onLoadMore = {
            onLoadMore()
        },
    )

    mediaList?.let {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(110.dp),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
            state = gridState,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        ) {
            items(mediaList) { mediaItem ->
                when (mediaItem) {
                    is MediaListItem.MediaListType -> {
                        val media = mediaItem.media
                        val coverImage =
                            rememberAsyncImagePainter(
                                model = media.coverImage.large,
                            )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier =
                                Modifier.clickable {
                                    media.type?.let { type ->
                                        navActionManager.toMediaDetail(
                                            id = media.idAniList,
                                            mediaType = type,
                                        )
                                    }
                                },
                        ) {
                            ImageCard(
                                painter = coverImage,
                                score = (media.meanScore.toDouble()) / 10,
                                isAnime = media.type == MediaType.ANIME,
                                totalChapters = media.chapters,
                                totalEpisodes = media.episodes,
                                releasedEpisodes = media.nextAiringEpisode?.episode?.minus(1),
                                format = media.format?.name,
                            )

                            OtakuImageCardTitle(title = media.title.english.ifBlank { media.title.romaji })
                        }
                    }

                    is MediaListItem.ScheduleType -> {
                        val media = mediaItem.schedule
                        val coverImage =
                            rememberAsyncImagePainter(
                                model = media.media.coverImage.large,
                            )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier =
                                Modifier.clickable {
                                    media.media.type?.let { type ->
                                        navActionManager.toMediaDetail(
                                            id = media.media.idAniList,
                                            mediaType = type,
                                        )
                                    }
                                },
                        ) {
                            ImageCard(
                                painter = coverImage,
                                score = (media.media.meanScore.toDouble()) / 10,
                                isAnime = media.media.type == MediaType.ANIME,
                                totalChapters = media.media.chapters,
                                totalEpisodes = media.media.episodes,
                                releasedEpisodes = media.episode,
                                format = media.media.format?.name,
                            )

                            OtakuImageCardTitle(title = media.media.title.english.ifBlank { media.media.title.romaji })
                        }
                    }
                }
            }
        }
    }
}
