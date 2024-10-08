package com.example.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.navigation.NavActionManager

@Composable
fun MediaSearchView(
    navActionManager: NavActionManager,
    mediaSearchViewModel: MediaSearchViewModel = hiltViewModel(),
) {
    val uiState by mediaSearchViewModel.state.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
        }
    }
}
