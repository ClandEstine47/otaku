package com.example.feature.medialist

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.core.navigation.NavActionManager

@Composable
fun MediaListView(
    navActionManager: NavActionManager,
    animeViewModel: MediaListViewViewModel = hiltViewModel(),
) {
    Text(text = "Media List View")
}
