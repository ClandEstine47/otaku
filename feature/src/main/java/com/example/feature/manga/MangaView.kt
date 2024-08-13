package com.example.feature.manga

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.navigation.NavActionManager

@Composable
fun MangaView(
    navActionManager: NavActionManager,
    mangaViewModel: MangaViewModel = hiltViewModel(),
) {
    Column {
        Text(text = "Manga View")
    }
}
