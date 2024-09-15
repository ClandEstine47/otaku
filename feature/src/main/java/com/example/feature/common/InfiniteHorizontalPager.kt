package com.example.feature.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.media.Media
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteHorizontalPager(mediaList: List<Media>) {
    val pagerState = rememberPagerState { Int.MAX_VALUE }

    // infinite scroll
    LaunchedEffect(key1 = Unit) {
        var initPage = Int.MAX_VALUE / 2
        while (initPage % mediaList.size != 0) {
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
            ) { index ->
                mediaList.getOrNull(
                    index % (mediaList.size),
                )?.let { media ->
                    BannerItem(media = media)
                }
            }
        }
    }
}
