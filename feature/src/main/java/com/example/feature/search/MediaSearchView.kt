package com.example.feature.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    val scope = rememberCoroutineScope()
    var inputText by rememberSaveable {
        mutableStateOf("")
    }
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    Scaffold(
        modifier =
            Modifier
                .absolutePadding()
                .padding(horizontal = 5.dp),
    ) { innerPadding ->

        if (openBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    openBottomSheet = false
                },
                sheetState = bottomSheetState,
                content = {
                    MediaFilter(
                        mediaType = arguments.mediaType,
                    )
                },
            )
        }

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
                        onExpandedChange = {
                            expanded = false
                            isClicked = it
                        },
                        placeholder = {
                            if (!isClicked) {
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
                                    tint = if (isClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
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
                                    tint = if (isClicked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier =
                                        Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                openBottomSheet = true
                                            },
                                )
                            }
                        },
                        modifier = Modifier.padding(vertical = 0.dp),
                    )
                },
                expanded = expanded,
                onExpandedChange = {
                    expanded = false
                    isClicked = it
                },
                colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)),
                modifier = Modifier,
            ) {}
        }
    }
}

@Composable
private fun MediaFilter(
    mediaType: MediaType,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 10.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "reset",
            tint = MaterialTheme.colorScheme.primary,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable {
                        // todo: reset values
                    },
        )
        OtakuTitle(
            id = R.string.filter,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.region_search),
                contentDescription = "region",
                tint = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable {
                            // todo: select regions
                        },
            )
            Icon(
                painter = painterResource(id = R.drawable.filter),
                contentDescription = "filter",
                tint = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable {
                            // todo: select filter by
                        },
            )
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OtakuDropdownMenu(
            options = listOf(),
            label = stringResource(id = R.string.year),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                // todo: save current selected value
            },
        )
        if (mediaType == MediaType.ANIME) {
            OtakuDropdownMenu(
                options = listOf(),
                label = stringResource(id = R.string.season),
                modifier = Modifier.weight(1f),
                onValueChangedEvent = { value ->
                    // todo: save current selected value
                },
            )
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OtakuDropdownMenu(
            options = listOf(),
            label = stringResource(id = R.string.format),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                // todo: save current selected value
            },
        )
        OtakuDropdownMenu(
            options = listOf(),
            label = stringResource(id = R.string.status),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                // todo: save current selected value
            },
        )
    }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TextButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f),
        ) {
            OtakuTitle(
                id = R.string.cancel,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        TextButton(
            onClick = { /*TODO*/ },
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f),
        ) {
            OtakuTitle(
                id = R.string.apply,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}
