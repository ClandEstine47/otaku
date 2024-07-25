package com.example.feature.anime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AnimeScreen(state: AnimeViewModel.AnimeUiState) {
    Column {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            state.trendingNowMedia?.forEach { media ->
                Row {
                    Text(text = media.title.romaji)
                }
            }
        }
    }
}
