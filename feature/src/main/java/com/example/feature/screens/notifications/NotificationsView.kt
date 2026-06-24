package com.example.feature.screens.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.notification.Notification
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.Utils
import com.example.feature.common.ErrorScreen
import com.example.feature.common.OnBottomReached
import com.example.feature.common.OtakuTitle
import kotlinx.coroutines.launch
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel as hiltComposeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsView(
    navActionManager: NavActionManager,
    viewModel: NotificationsViewModel = hiltComposeViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val pagerState =
        rememberPagerState(initialPage = uiState.selectedTab.ordinal) {
            NotificationTab.entries.size
        }

    // Sync Pager -> ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val selectedTab = NotificationTab.entries.getOrNull(page)
            if (selectedTab != null) {
                viewModel.onTabSelected(selectedTab)
            }
        }
    }

    // Sync ViewModel -> Pager
    LaunchedEffect(uiState.selectedTab) {
        if (pagerState.currentPage != uiState.selectedTab.ordinal) {
            pagerState.animateScrollToPage(uiState.selectedTab.ordinal)
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
                        .padding(start = 15.dp, bottom = 15.dp, end = 5.dp),
            ) {
                OtakuTitle(
                    id = R.string.notifications,
                    style = MaterialTheme.typography.titleLarge,
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart),
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(5.dp),
        ) {
            PrimaryScrollableTabRow(
                modifier = Modifier.padding(bottom = 10.dp),
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 5.dp,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = {
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(
                            selectedTabIndex = pagerState.currentPage,
                            matchContentSize = true,
                        ),
                        height = 3.dp,
                    )
                },
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                },
                tabs = {
                    NotificationTab.entries.forEach { tab ->
                        val isSelected = pagerState.currentPage == tab.ordinal

                        Tab(
                            selected = isSelected,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(tab.ordinal)
                                }
                            },
                            text = {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    OtakuTitle(
                                        title = tab.title.uppercase(),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            },
                        )
                    }
                },
            )

            HorizontalPager(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                state = pagerState,
                verticalAlignment = Alignment.Top,
            ) { page ->
                val tab = NotificationTab.entries[page]
                val tabState = uiState.tabs[tab] ?: NotificationTabState()

                NotificationList(
                    tabState = tabState,
                    onLoadMore = { viewModel.loadNotifications(tab = tab) },
                    onRetry = { viewModel.loadNotifications(isRefresh = true, tab = tab) },
                    onNotificationClick = { notification ->
                        handleNotificationClick(notification, navActionManager)
                    },
                )
            }
        }
    }
}

@Composable
private fun NotificationList(
    tabState: NotificationTabState,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (tabState.hasNextPage && !tabState.isLoading) {
            onLoadMore()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (tabState.isLoading && tabState.notifications.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (tabState.error != null && tabState.notifications.isEmpty()) {
            ErrorScreen(onRetryClick = onRetry)
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(tabState.notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }

                if (tabState.hasNextPage && tabState.isLoading) {
                    item {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

private fun handleNotificationClick(
    notification: Notification,
    navActionManager: NavActionManager,
) {
    when (notification) {
        is Notification.Airing -> {
            navActionManager.toMediaDetail(
                notification.animeId,
                notification.media.type ?: MediaType.ANIME,
            )
        }

        is Notification.RelatedMediaAddition -> {
            navActionManager.toMediaDetail(
                notification.mediaId,
                notification.media.type ?: MediaType.ANIME,
            )
        }

        is Notification.MediaDataChange -> {
            navActionManager.toMediaDetail(
                notification.mediaId,
                notification.media.type ?: MediaType.ANIME,
            )
        }

        is Notification.MediaMerge -> {
            navActionManager.toMediaDetail(
                notification.mediaId,
                notification.media.type ?: MediaType.ANIME,
            )
        }

        else -> {}
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageUrl =
            when (notification) {
                is Notification.Airing -> notification.media.coverImage.medium
                is Notification.RelatedMediaAddition -> notification.media.coverImage.medium
                is Notification.MediaDataChange -> notification.media.coverImage.medium
                is Notification.MediaMerge -> notification.media.coverImage.medium
                else -> null
            }

        if (imageUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            val title =
                when (notification) {
                    is Notification.Airing -> {
                        val episodeString = notification.contexts?.getOrNull(0).orEmpty()
                        val ofString = notification.contexts?.getOrNull(1).orEmpty()
                        val airedString = notification.contexts?.getOrNull(2).orEmpty()
                        "$episodeString${notification.episode}$ofString${notification.media.title.userPreferred}$airedString"
                    }

                    is Notification.RelatedMediaAddition -> {
                        "${notification.media.title.userPreferred} ${notification.context.orEmpty()}"
                    }

                    is Notification.MediaDataChange -> {
                        "${notification.media.title.userPreferred} ${notification.context.orEmpty()}\n${notification.reason.orEmpty()}"
                    }

                    is Notification.MediaMerge -> {
                        "${notification.media.title.userPreferred} ${notification.context.orEmpty()}"
                    }

                    is Notification.MediaDeletion -> {
                        "${notification.deletedMediaTitle.orEmpty()} ${notification.context.orEmpty()}"
                    }

                    else -> {
                        "Unknown notification"
                    }
                }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = Utils.formatTimeAgo(notification.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
    }
}
