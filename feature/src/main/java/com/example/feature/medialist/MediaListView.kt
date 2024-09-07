package com.example.feature.medialist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.anime.ImageCard
import com.example.feature.anime.OtakuImageCardTitle
import com.example.feature.anime.OtakuTitle

enum class ViewType {
    LIST,
    GRID,
}

@Composable
fun MediaListView(
    arguments: OtakuScreen.MediaList,
    navActionManager: NavActionManager,
    animeViewModel: MediaListViewViewModel = hiltViewModel(),
) {
    val uiState by animeViewModel.state.collectAsStateWithLifecycle()
    var viewType by rememberSaveable {
        mutableStateOf(ViewType.LIST)
    }

    LaunchedEffect(Unit) {
        animeViewModel.loadMediaList(
            mediaType = arguments.mediaType,
            contentType = arguments.contentType,
        )
    }

    Scaffold(
        modifier =
            Modifier
                .padding(top = 50.dp)
                .padding(horizontal = 5.dp),
        topBar = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp, bottom = 20.dp, end = 5.dp),
            ) {
                OtakuTitle(
                    id = arguments.titleId,
                    style = MaterialTheme.typography.titleLarge,
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart),
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconButton(
                        onClick = {
                            viewType = ViewType.LIST
                        },
                        colors =
                            if (viewType == ViewType.LIST) {
                                IconButtonDefaults.iconButtonColors()
                            } else {
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                )
                            },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "List View",
                        )
                    }

                    IconButton(
                        onClick = {
                            viewType = ViewType.GRID
                        },
                        colors =
                            if (viewType == ViewType.GRID) {
                                IconButtonDefaults.iconButtonColors()
                            } else {
                                IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                )
                            },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.grid_view_24px),
                            contentDescription = "Grid View",
                        )
                    }
                }
            }
        },
    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                when (viewType) {
                    ViewType.LIST -> {
                        MediaListViewContent(
                            navActionManager = navActionManager,
                            mediaList = uiState.mediaList,
                        )
                    }
                    ViewType.GRID -> {
                        MediaGridViewContent(
                            navActionManager = navActionManager,
                            mediaList = uiState.mediaList,
                        )
                    }
                }
            }
        }
    }
}

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
                        MediaListItem(mediaItem = mediaItem.media)
                    }
                    is MediaListItem.ScheduleType -> {
                        MediaListItem(mediaItem = mediaItem.schedule)
                    }
                }
            }
        }
    }
}

@Composable
fun MediaGridViewContent(
    navActionManager: NavActionManager,
    mediaList: List<MediaListItem>? = null,
) {
    mediaList?.let {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(110.dp),
            modifier = Modifier.padding(horizontal = 5.dp),
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
                        val media = mediaItem.schedule.media
                        val coverImage =
                            rememberAsyncImagePainter(
                                model = media.coverImage.large,
                            )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
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
                }
            }
        }
    }
}
