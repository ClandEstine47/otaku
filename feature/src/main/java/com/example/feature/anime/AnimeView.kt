package com.example.feature.anime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AnimeView(animeViewModel: AnimeViewModel = hiltViewModel()) {
    val uiState by animeViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            uiState.trendingNowMedia?.forEach { media ->
                Row {
                    Text(text = media.title.romaji)
                }
            }
        }
    }
}
