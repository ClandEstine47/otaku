package com.example.feature.medialist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import kotlinx.coroutines.launch

enum class ViewType {
    LIST,
    GRID,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListView(
    arguments: OtakuScreen.MediaList,
    navActionManager: NavActionManager,
    mediaListViewModel: MediaListViewViewModel = hiltViewModel(),
) {
    val uiState by mediaListViewModel.state.collectAsStateWithLifecycle()
    var viewType by rememberSaveable {
        mutableStateOf(ViewType.LIST)
    }
    val coroutineScope = rememberCoroutineScope()

    val daysSorted = mediaListViewModel.getSortedCalendarTabs()
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState(initialPage = selectedTabIndex) { daysSorted.size }

    LaunchedEffect(Unit) {
        mediaListViewModel.loadMediaList(
            mediaType = arguments.mediaType,
            contentType = arguments.contentType,
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            mediaListViewModel.setDayOffset(page)
            mediaListViewModel.loadMediaListByDay(dayIndex = page)
            selectedTabIndex = page
        }
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
                        .padding(start = 5.dp, bottom = 15.dp, end = 5.dp),
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
            PrimaryScrollableTabRow(
                modifier = Modifier.padding(bottom = 10.dp),
                containerColor = MaterialTheme.colorScheme.background,
                edgePadding = 5.dp,
                selectedTabIndex = selectedTabIndex,
                indicator = {
                    SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(selectedTabIndex = selectedTabIndex, matchContentSize = true),
                        height = 3.dp,
                    )
                },
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                },
                tabs = {
                    val tabWidthModifier = Modifier.width(IntrinsicSize.Max)
                    daysSorted.forEachIndexed { index, day ->
                        val isCurrentTabSelected = selectedTabIndex == index

                        Tab(
                            selected = isCurrentTabSelected,
                            onClick = {
                                selectedTabIndex = index
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = tabWidthModifier,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                OtakuTitle(
                                    title = stringResource(id = day.stringRes),
                                    color = if (isCurrentTabSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }
                },
            )

            HorizontalPager(
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->

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
                                mediaList = uiState.mediaListByPage[page],
                            )
                        }
                        ViewType.GRID -> {
                            MediaGridViewContent(
                                navActionManager = navActionManager,
                                mediaList = uiState.mediaListByPage[page],
                            )
                        }
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
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
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
                        val media = mediaItem.schedule
                        val coverImage =
                            rememberAsyncImagePainter(
                                model = media.media.coverImage.large,
                            )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
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
