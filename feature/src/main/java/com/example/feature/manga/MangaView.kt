package com.example.feature.manga

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.media.Media
import com.example.core.navigation.NavActionManager
import com.example.feature.anime.InfiniteHorizontalPager

@Composable
fun MangaView(
    navActionManager: NavActionManager,
    mangaViewModel: MangaViewModel = hiltViewModel(),
) {
    val uiState by mangaViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .absolutePadding()
                .verticalScroll(rememberScrollState()),
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
            MangaContent(
                navActionManager = navActionManager,
                trendingNowMedia = uiState.trendingNowMedia,
            )
        }
    }
}

@Composable
fun MangaContent(
    navActionManager: NavActionManager,
    trendingNowMedia: List<Media>? = null,
) {
    if (trendingNowMedia != null) {
        InfiniteHorizontalPager(mediaList = trendingNowMedia)
    }

    Spacer(modifier = Modifier.height(40.dp))
}
