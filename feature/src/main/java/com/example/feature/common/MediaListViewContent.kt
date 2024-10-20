package com.example.feature.common

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.core.navigation.NavActionManager
import com.example.feature.medialist.MediaListItem

@Composable
fun MediaListViewContent(
    navActionManager: NavActionManager,
    mediaList: List<MediaListItem>? = null,
) {
    mediaList?.let {
        LazyColumn {
            items(mediaList) { mediaItem ->
                when (mediaItem) {
                    is MediaListItem.MediaListType -> {
                        MediaListItem(
                            mediaItem = mediaItem.media,
                            onClick = { id, type ->
                                navActionManager.toMediaDetail(
                                    id = id,
                                    mediaType = type,
                                )
                            },
                        )
                    }

                    is MediaListItem.ScheduleType -> {
                        MediaListItem(
                            mediaItem = mediaItem.schedule,
                            onClick = { id, type ->
                                navActionManager.toMediaDetail(
                                    id = id,
                                    mediaType = type,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
