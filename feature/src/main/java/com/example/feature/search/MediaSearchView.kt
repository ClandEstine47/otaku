package com.example.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.anime.OtakuTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSearchView(
    arguments: OtakuScreen.MediaSearch,
    navActionManager: NavActionManager,
    mediaSearchViewModel: MediaSearchViewModel = hiltViewModel(),
) {
    val uiState by mediaSearchViewModel.state.collectAsStateWithLifecycle()
    var inputText by rememberSaveable {
        mutableStateOf("")
    }
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        modifier =
            Modifier
                .absolutePadding()
                .padding(horizontal = 5.dp),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding.calculateBottomPadding())
                    .padding(5.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = inputText,
                        onQueryChange = { inputText = it },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expanded = false },
                        placeholder = {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                OtakuTitle(
                                    id = if (arguments.mediaType == MediaType.ANIME) R.string.anime else R.string.manga,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                )
                            }
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                )
                            }
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier.fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.filter),
                                    contentDescription = "search",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier =
                                        Modifier
                                            .clickable {
                                                // todo: open media filters
                                            },
                                )
                            }
                        },
                        modifier = Modifier.padding(vertical = 0.dp),
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = false },
                colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
                modifier = Modifier,
            ) {}
        }
    }
}
