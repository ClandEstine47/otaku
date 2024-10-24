package com.example.feature.search

import android.os.Handler
import android.os.Looper
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.Countries
import com.example.core.domain.model.media.MediaFormat
import com.example.core.domain.model.media.MediaSeason
import com.example.core.domain.model.media.MediaSort
import com.example.core.domain.model.media.MediaStatus
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.Utils
import com.example.feature.anime.OtakuTitle
import com.example.feature.common.MediaGridViewContent
import com.example.feature.common.MediaListItem
import com.example.feature.common.MediaListViewContent
import com.example.feature.common.ViewType
import java.time.LocalDate

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

    var selectedCountry by rememberSaveable {
        mutableStateOf<Countries?>(null)
    }
    var selectedSort by rememberSaveable {
        mutableStateOf<MediaSort?>(null)
    }
    var selectedYear by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    var selectedSeason by rememberSaveable {
        mutableStateOf<MediaSeason?>(null)
    }
    var selectedFormat by rememberSaveable {
        mutableStateOf<MediaFormat?>(null)
    }
    var selectedStatus by rememberSaveable {
        mutableStateOf<MediaStatus?>(null)
    }
    var viewType by rememberSaveable {
        mutableStateOf(ViewType.LIST)
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val focusManager = LocalFocusManager.current
    val handler = Handler(Looper.getMainLooper())
    val searchRunnable =
        Runnable {
            mediaSearchViewModel.loadSearchResult(
                mediaType = arguments.mediaType,
                searchQuery = inputText,
                season = selectedSeason,
                seasonYear = selectedYear,
                format = selectedFormat,
                status = selectedStatus,
                countryOfOrigin = selectedCountry?.code,
                genres = null,
                tags = null,
                sortBy = selectedSort,
            )
        }

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
                        country = selectedCountry,
                        sort = selectedSort,
                        year = selectedYear,
                        season = selectedSeason,
                        format = selectedFormat,
                        status = selectedStatus,
                        onCancelClick = {
                            openBottomSheet = false
                        },
                        onApplyClick = { country, sortBy, year, season, format, status ->

                            selectedCountry = country
                            selectedSort = sortBy
                            selectedYear = year
                            selectedSeason = season
                            selectedFormat = format
                            selectedStatus = status

                            mediaSearchViewModel.loadSearchResult(
                                mediaType = arguments.mediaType,
                                searchQuery = inputText,
                                season = selectedSeason,
                                seasonYear = selectedYear,
                                format = selectedFormat,
                                status = selectedStatus,
                                countryOfOrigin = selectedCountry?.code,
                                genres = null,
                                tags = null,
                                sortBy = selectedSort,
                            )
                            openBottomSheet = false
                        },
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
                        onQueryChange = { query ->
                            inputText = query
                            handler.removeCallbacks(searchRunnable)
                            handler.postDelayed(searchRunnable, 500L)
                        },
                        onSearch = {
                            expanded = false
                            focusManager.clearFocus()
                        },
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
                if (!uiState.mediaList.isNullOrEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp, bottom = 15.dp, end = 5.dp, top = 15.dp),
                    ) {
                        OtakuTitle(
                            id = R.string.search_results,
                            modifier =
                                Modifier
                                    .align(Alignment.CenterStart),
                        )

                        Row(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            IconButton(
                                onClick = {
                                    viewType = ViewType.LIST
                                },
                                colors =
                                    if (viewType == ViewType.LIST) {
                                        IconButtonDefaults.iconButtonColors()
                                    } else {
                                        IconButtonDefaults.iconButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        )
                                    },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.List,
                                    contentDescription = "List View",
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewType = ViewType.GRID
                                },
                                colors =
                                    if (viewType == ViewType.GRID) {
                                        IconButtonDefaults.iconButtonColors()
                                    } else {
                                        IconButtonDefaults.iconButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                        )
                                    },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.grid_view_24px),
                                    contentDescription = "Grid View",
                                )
                            }
                        }
                    }

                    when (viewType) {
                        ViewType.LIST -> {
                            MediaListViewContent(
                                navActionManager = navActionManager,
                                mediaList = uiState.mediaList!!.map { MediaListItem.MediaListType(it) },
                                onLoadMore = {
                                    mediaSearchViewModel.incrementPageNumber()
                                    mediaSearchViewModel.loadSearchResult(
                                        mediaType = arguments.mediaType,
                                        searchQuery = inputText,
                                        season = selectedSeason,
                                        seasonYear = selectedYear,
                                        format = selectedFormat,
                                        status = selectedStatus,
                                        countryOfOrigin = selectedCountry?.code,
                                        genres = null,
                                        tags = null,
                                        sortBy = selectedSort,
                                        loadMore = true,
                                    )
                                },
                            )
                        }
                        ViewType.GRID -> {
                            MediaGridViewContent(
                                navActionManager = navActionManager,
                                mediaList = uiState.mediaList!!.map { MediaListItem.MediaListType(it) },
                                onLoadMore = {
                                    mediaSearchViewModel.incrementPageNumber()
                                    mediaSearchViewModel.loadSearchResult(
                                        mediaType = arguments.mediaType,
                                        searchQuery = inputText,
                                        season = selectedSeason,
                                        seasonYear = selectedYear,
                                        format = selectedFormat,
                                        status = selectedStatus,
                                        countryOfOrigin = selectedCountry?.code,
                                        genres = null,
                                        tags = null,
                                        sortBy = selectedSort,
                                        loadMore = true,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaFilter(
    mediaType: MediaType,
    country: Countries?,
    sort: MediaSort?,
    year: Int?,
    season: MediaSeason?,
    format: MediaFormat?,
    status: MediaStatus?,
    onCancelClick: () -> Unit,
    onApplyClick: (
        country: Countries?,
        sort: MediaSort?,
        year: Int?,
        season: MediaSeason?,
        format: MediaFormat?,
        status: MediaStatus?,
    ) -> Unit,
) {
    val startYear = 1940
    val endYear = LocalDate.now().year + 1
    val yearList = (startYear..endYear).map { it.toString() }.reversed()
    val seasons = MediaSeason.validSeasons
    val animeFormat = MediaFormat.animeFormats
    val mangaFormat = MediaFormat.mangaFormats
    val statusList = MediaStatus.statusList
    val countries = Countries.countries
    val sortBy = MediaSort.types

    var selectedCountry by rememberSaveable {
        mutableStateOf(country)
    }
    var selectedSort by rememberSaveable {
        mutableStateOf(sort)
    }
    var selectedYear by rememberSaveable {
        mutableStateOf(year)
    }
    var selectedSeason by rememberSaveable {
        mutableStateOf(season)
    }
    var selectedFormat by rememberSaveable {
        mutableStateOf(format)
    }
    var selectedStatus by rememberSaveable {
        mutableStateOf(status)
    }

    var expandRegion by remember {
        mutableStateOf(false)
    }
    var expandSortBy by remember {
        mutableStateOf(false)
    }

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
                        selectedYear = null
                        selectedSeason = null
                        selectedFormat = null
                        selectedStatus = null
                        selectedCountry = null
                        selectedSort = null
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
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.region_search),
                    contentDescription = "region",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable {
                                expandRegion = true
                            },
                )

                DropdownMenu(
                    expanded = expandRegion,
                    onDismissRequest = {
                        expandRegion = false
                    },
                ) {
                    countries.forEach { option: Countries ->
                        DropdownMenuItem(
                            text = {
                                OtakuTitle(
                                    title = Utils.getFormattedString(option),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedCountry == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                )
                            },
                            onClick = {
                                expandRegion = false
                                selectedCountry = option
                            },
                        )
                    }
                }
            }

            Box {
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "filter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable {
                                expandSortBy = true
                            },
                )

                DropdownMenu(
                    expanded = expandSortBy,
                    onDismissRequest = {
                        expandSortBy = false
                    },
                ) {
                    sortBy.forEach { option: MediaSort ->
                        DropdownMenuItem(
                            text = {
                                OtakuTitle(
                                    title = Utils.getFormattedString(option),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selectedSort == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                )
                            },
                            onClick = {
                                expandSortBy = false
                                selectedSort = option
                            },
                        )
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OtakuDropdownMenu(
            options = yearList,
            currentValue = selectedYear?.toString(),
            label = stringResource(id = R.string.year),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                selectedYear = value.toIntOrNull()
            },
        )
        if (mediaType == MediaType.ANIME) {
            OtakuDropdownMenu(
                options = seasons,
                currentValue = selectedSeason,
                label = stringResource(id = R.string.season),
                modifier = Modifier.weight(1f),
                onValueChangedEvent = { value ->
                    selectedSeason = value
                },
            )
        }
    }

    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OtakuDropdownMenu(
            options = if (mediaType == MediaType.ANIME) animeFormat else mangaFormat,
            currentValue = selectedFormat,
            label = stringResource(id = R.string.format),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                selectedFormat = value
            },
        )
        OtakuDropdownMenu(
            options = statusList,
            currentValue = selectedStatus,
            label = stringResource(id = R.string.status),
            modifier = Modifier.weight(1f),
            onValueChangedEvent = { value ->
                selectedStatus = value
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
            onClick = {
                onCancelClick()
            },
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
            onClick = {
                onApplyClick(
                    selectedCountry,
                    selectedSort,
                    selectedYear,
                    selectedSeason,
                    selectedFormat,
                    selectedStatus,
                )
            },
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
