package com.example.feature.medialist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.core.domain.model.MediaListContentType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.anime.OtakuTitle
import com.example.feature.common.MediaGridViewContent
import com.example.feature.common.MediaListViewContent
import com.example.feature.common.ViewType
import kotlinx.coroutines.launch

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
    val isCalendarMediaList = arguments.contentType == MediaListContentType.RECENTLY_UPDATED
    val pagerState = rememberPagerState(initialPage = selectedTabIndex) { if (isCalendarMediaList) daysSorted.size else 1 }

    LaunchedEffect(Unit) {
        mediaListViewModel.loadMediaList(
            mediaType = arguments.mediaType,
            contentType = arguments.contentType,
        )
    }

    LaunchedEffect(pagerState) {
        if (isCalendarMediaList) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                mediaListViewModel.setDayOffset(page)
                mediaListViewModel.loadMediaListByDay(dayIndex = page)
                selectedTabIndex = page
            }
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
            if (isCalendarMediaList) {
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
            } else {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Previous Button
                    PageSelectorButton(
                        buttonIcon = "<",
                        enabled = (uiState.pageNumber > 1 && !uiState.isLoading),
                        onClick = {
                            mediaListViewModel.decreasePageNumber()
                            mediaListViewModel.loadMediaList(
                                mediaType = arguments.mediaType,
                                contentType = arguments.contentType,
                            )
                        },
                    )

                    PageNumberButton(pageNumber = uiState.pageNumber)

                    // Next Button
                    PageSelectorButton(
                        buttonIcon = ">",
                        enabled = (uiState.hasNextPage == true && !uiState.isLoading),
                        onClick = {
                            mediaListViewModel.increasePageNumber()
                            mediaListViewModel.loadMediaList(
                                mediaType = arguments.mediaType,
                                contentType = arguments.contentType,
                            )
                        },
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))
            }

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
