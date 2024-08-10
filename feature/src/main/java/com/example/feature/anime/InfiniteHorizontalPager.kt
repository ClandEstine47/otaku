package com.example.feature.anime

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.core.domain.model.media.Media
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteHorizontalPager(currentSeasonMedia: List<Media>) {
    val pagerState = rememberPagerState { Int.MAX_VALUE }

    // infinite scroll
    LaunchedEffect(key1 = Unit) {
        var initPage = Int.MAX_VALUE / 2
        while (initPage % currentSeasonMedia.size != 0) {
            initPage++
        }
        pagerState.scrollToPage(initPage)
    }

    // auto scroll
    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (true) {
                delay(5000L)

                withContext(NonCancellable) {
                    if (pagerState.currentPage + 1 in 0..Int.MAX_VALUE) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        val initPage = Int.MAX_VALUE / 2
                        pagerState.scrollToPage(initPage)
                    }
                }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(0.dp),
            ) { index ->
                currentSeasonMedia.getOrNull(
                    index % (currentSeasonMedia.size),
                )?.let { anime ->
                    BannerItem(anime = anime)
                }
            }
        }
    }
}

@Composable
fun BannerItem(anime: Media) {
    val bannerPainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(anime.bannerImage)
                    .crossfade(true)
                    .build(),
        )

    val coverPainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(anime.coverImage.large)
                    .crossfade(true)
                    .build(),
        )

    AnimatedBannerImage(
        bannerPainter = bannerPainter,
        coverPainter = coverPainter,
        score = (anime.meanScore.toDouble() / 10),
    )
}
