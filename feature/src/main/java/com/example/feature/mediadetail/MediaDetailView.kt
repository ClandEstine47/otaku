package com.example.feature.mediadetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.media.Media
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.anime.BackButton
import com.example.feature.anime.OtakuTitle
import com.example.feature.anime.ShareButton
import com.example.feature.common.BannerItem

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailContent(
    navActionManager: NavActionManager,
    mediaDetail: Media?,
) {
    val topAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(
            rememberTopAppBarState(),
        )
    val isTopAppBarScrolled by remember {
        derivedStateOf { topAppBarScrollBehavior.state.overlappedFraction == 1f }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isTopAppBarScrolled) {
                        Text(
                            text = "TITLE",
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    BackButton(
                        modifier = Modifier,
                        onButtonClick = {
                            // todo: navigate back
                        },
                    )
                },
                actions = {
                    ShareButton(
                        modifier = Modifier,
                        onButtonClick = {
                            // todo: share
                        },
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
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
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
                    verticalArrangement = Arrangement.spacedBy(20.dp),
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

                    OtakuTitle(id = R.string.info)
                }
            }
        }
    }
}
