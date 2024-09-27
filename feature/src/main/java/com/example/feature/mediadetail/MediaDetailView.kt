package com.example.feature.mediadetail

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.StatusDistribution
import com.example.core.domain.model.media.Media
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.anime.BackButton
import com.example.feature.anime.OtakuTitle
import com.example.feature.anime.ShareButton
import com.example.feature.common.BannerItem
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

@Composable
fun MediaDetailView(
    arguments: OtakuScreen.MediaDetail,
    navActionManager: NavActionManager,
    viewModel: MediaDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getMediaDetail(id = arguments.id)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .absolutePadding(),
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
            MediaDetailContent(
                navActionManager = navActionManager,
                mediaDetail = uiState.media,
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailContent(
    navActionManager: NavActionManager,
    mediaDetail: Media?,
) {
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(
            rememberTopAppBarState(),
        )
    val isTopAppBarScrolled by remember {
        derivedStateOf { topAppBarScrollBehavior.state.overlappedFraction == 1f }
    }
    var currentBottomTab by rememberSaveable {
        mutableStateOf(MediaDetailType.INFO)
    }

    val navBarItems =
        listOf(
            MediaDetailNavBarItem(mediaDetailType = MediaDetailType.INFO, icon = R.drawable.info),
            MediaDetailNavBarItem(mediaDetailType = MediaDetailType.GROUP, icon = R.drawable.group),
            MediaDetailNavBarItem(mediaDetailType = MediaDetailType.STATS, icon = R.drawable.stats),
            MediaDetailNavBarItem(mediaDetailType = MediaDetailType.SOCIAL, icon = R.drawable.social),
        )

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (isTopAppBarScrolled) {
                        OtakuTitle(
                            title = mediaDetail?.title?.english?.ifBlank { mediaDetail.title.romaji } ?: "",
                            color = MaterialTheme.colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    BackButton(
                        modifier = Modifier,
                        onButtonClick = {
                            navActionManager.navigateBack()
                        },
                    )
                },
                actions = {
                    ShareButton(
                        modifier = Modifier,
                        url = mediaDetail?.siteUrl.orEmpty(),
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                    ),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        bottomBar = {
            MediaDetailBottomNavBar(
                navBarItems = navBarItems,
                hazeState = hazeState,
                navigate = { mediaDetailType ->
                    currentBottomTab =
                        when (mediaDetailType) {
                            MediaDetailType.INFO -> MediaDetailType.INFO
                            MediaDetailType.GROUP -> MediaDetailType.GROUP
                            MediaDetailType.STATS -> MediaDetailType.STATS
                            MediaDetailType.SOCIAL -> MediaDetailType.SOCIAL
                        }
                },
            )
        },
        content = {
            Column(
                modifier =
                    Modifier
                        .haze(
                            hazeState,
                            HazeStyle(
                                tint = MaterialTheme.colorScheme.background.copy(alpha = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) .2f else .8f),
                                blurRadius = 30.dp,
                                noiseFactor = HazeDefaults.noiseFactor,
                            ),
                        )
                        .fillMaxSize()
                        .absolutePadding()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                mediaDetail?.let { media ->
                    BannerItem(
                        media = media,
                        rankingVisibility = true,
                        descriptionVisibility = true,
                        onBannerItemClick = {},
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(30.dp),
                    ) {
                        /* if (media.status == MediaStatus.RELEASING) {
                            val releaseDate = "Ep. ${media.nextAiringEpisode?.episode} on ${Utils.displayInDayDateTimeFormat(media.nextAiringEpisode?.airingAt ?: 0)}"

                            OtakuTitle(
                                title = releaseDate,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        } */

                        when (currentBottomTab) {
                            MediaDetailType.INFO -> MediaInfoTab(media = media, navActionManager = navActionManager)
                            MediaDetailType.GROUP -> MediaGroupTab(media = media, navActionManager = navActionManager)
                            MediaDetailType.STATS -> MediaStatsTab(media = media, navActionManager = navActionManager)
                            MediaDetailType.SOCIAL -> {}
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        },
    )
}

@Composable
private fun MediaInfoTab(
    media: Media,
    navActionManager: NavActionManager,
) {
    var showSpoilerTags by remember {
        mutableStateOf(false)
    }
    media.trailer?.let { trailer ->
        trailer.id?.let { videoId ->
            trailer.thumbnail?.let { thumbnailUrl ->
                OtakuTitle(id = R.string.trailer)

                YouTubePlayer(
                    videoId = videoId,
                    thumbnailUrl = thumbnailUrl,
                )
            }
        }
    }

    // Media Info
    OtakuTitle(id = R.string.info)
    MediaInfo(
        media = media,
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OtakuTitle(id = R.string.tags)
        OtakuTitle(
            id = R.string.show_spoilers,
            color = if (showSpoilerTags) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            modifier =
                Modifier.clickable {
                    showSpoilerTags = !showSpoilerTags
                },
        )
    }

    media.tags?.let { mediaTags ->
        val spoilerFreeTags =
            mediaTags.filter { tag ->
                tag.isGeneralSpoiler != true
            }
        MediaTags(
            tags = if (showSpoilerTags) mediaTags else spoilerFreeTags,
        )
    }

    // Media Relations
    MediaRelations(
        mediaConnection = media.relations,
        navActionManager = navActionManager,
    )

    // Media Recommendations
    MediaRecommendations(
        mediaRecommendation = media.recommendations,
        navActionManager = navActionManager,
    )

    // External Links
    media.externalLinks?.let { links ->
        MediaExternalLinks(
            externalLinks = links,
        )
    }
}

@Composable
fun MediaGroupTab(
    media: Media,
    navActionManager: NavActionManager,
) {
    // Media Characters
    MediaCharacters(
        characters = media.characters,
        navActionManager = navActionManager,
    )

    // Media Staffs
    MediaStaffs(
        staffs = media.staff,
        navActionManager = navActionManager,
    )
}

@Composable
fun MediaStatsTab(
    media: Media,
    navActionManager: NavActionManager,
) {
    // Status Distribution
    media.stats?.statusDistribution?.let { status ->
        MediaStatusDistribution(
            status = status,
        )
    }
}

@Composable
fun MediaStatusDistribution(
    status: List<StatusDistribution>,
) {
    val chartData =
        status.associate {
            Pair(it.status?.name ?: "Unknown", it.amount ?: 0)
        }

    OtakuTitle(id = R.string.status_distribution)
    StatusDistributionChart(
        data = chartData,
    )
}
