package com.example.feature.common

import androidx.compose.foundation.layout.Arrangement
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

                        MediaItem(
                            media = media,
                            isAnime = media.type == MediaType.ANIME,
                            releasedEpisodes = media.nextAiringEpisode?.episode?.minus(1),
                            onClick = { id ->
                                media.type?.let { type ->
                                    navActionManager.toMediaDetail(
                                        id = id,
                                        mediaType = type,
                                    )
                                }
                            },
                        )
                    }

                    is MediaListItem.ScheduleType -> {
                        val media = mediaItem.schedule

                        MediaItem(
                            media = media.media,
                            isAnime = media.media.type == MediaType.ANIME,
                            releasedEpisodes = media.episode,
                            onClick = { id ->
                                media.media.type?.let { type ->
                                    navActionManager.toMediaDetail(
                                        id = id,
                                        mediaType = type,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
