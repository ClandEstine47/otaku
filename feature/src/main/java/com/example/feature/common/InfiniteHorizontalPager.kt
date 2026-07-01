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
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.core.domain.model.media.Media
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteHorizontalPager(
    mediaList: List<Media>,
    onBannerItemClick: (mediaId: Int) -> Unit,
) {
    if (mediaList.isEmpty()) return

    val scrollInterval = 5000L
    val anchorPage = Int.MAX_VALUE / 2

    fun targetPageFor(now: Long): Int {
        val size = mediaList.size
        val tickIndex = ((now / scrollInterval) % size).toInt()
        val anchorRemainder = anchorPage % size
        return anchorPage - anchorRemainder + tickIndex
    }

    val initialPage =
        remember(mediaList.size) {
            targetPageFor(System.currentTimeMillis())
        }
    val pagerState = rememberPagerState(initialPage = initialPage) { Int.MAX_VALUE }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Sync on return and handle auto scroll globally
    LaunchedEffect(pagerState, lifecycleOwner, mediaList.size) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            val initialTarget = targetPageFor(System.currentTimeMillis())
            if (pagerState.currentPage != initialTarget) {
                pagerState.scrollToPage(initialTarget)
            }

            // Resync immediately whenever a manual gesture (drag/fling) finishes,
            // instead of waiting for the next scheduled tick.
            launch {
                snapshotFlow { pagerState.isScrollInProgress }
                    .collect { inProgress ->
                        if (!inProgress) {
                            val target = targetPageFor(System.currentTimeMillis())
                            if (pagerState.currentPage != target) {
                                pagerState.animateScrollToPage(target)
                            }
                        }
                    }
            }

            // Periodic tick — advances to whatever page the current time slot maps to.
            while (true) {
                val currentTime = System.currentTimeMillis()
                val timeToNextTick = scrollInterval - (currentTime % scrollInterval)
                delay(timeToNextTick)

                val target = targetPageFor(System.currentTimeMillis())
                if (pagerState.currentPage != target && !pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(target)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
            ) { index ->
                mediaList
                    .getOrNull(index % mediaList.size)
                    ?.let { media ->
                        BannerItem(
                            media = media,
                            onBannerItemClick = { mediaId ->
                                onBannerItemClick(mediaId)
                            },
                        )
                    }
            }
        }
    }
}
