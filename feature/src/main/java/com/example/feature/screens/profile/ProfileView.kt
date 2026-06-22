package com.example.feature.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.user.User
import com.example.core.navigation.NavActionManager
import com.example.core.navigation.OtakuScreen
import com.example.feature.R
import com.example.feature.common.BackButton
import com.example.feature.common.BannerCard
import com.example.feature.common.ErrorScreen
import com.example.feature.common.OtakuTitle
import com.example.feature.common.ShareButton
import com.example.feature.common.TagItem

@Composable
fun ProfileView(
    arguments: OtakuScreen.Profile,
    navActionManager: NavActionManager,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.user != null -> {
                ProfileContent(
                    user = uiState.user!!,
                    navActionManager = navActionManager,
                )
            }

            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.error != null -> {
                ErrorScreen(
                    onRetryClick = {
                        arguments.userId?.let { viewModel.loadUserProfile(it) }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    user: User,
    navActionManager: NavActionManager,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val showFutureToast = {
        Toast.makeText(context, R.string.future_update, Toast.LENGTH_SHORT).show()
    }

    Scaffold { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(scrollState),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                // Banner
                val bannerPainter = rememberAsyncImagePainter(model = user.bannerImage)
                BannerCard(bannerPainter = bannerPainter, height = 232.dp)

                // Navigation Buttons (Scrolling with content)
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackButton(
                        modifier = Modifier,
                        onButtonClick = {
                            navActionManager.navigateBack()
                        },
                    )

                    ShareButton(
                        modifier = Modifier,
                        url = user.siteUrl,
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Avatar
                    Image(
                        painter = rememberAsyncImagePainter(model = user.avatar.large ?: user.avatar.medium),
                        contentDescription = "Avatar",
                        modifier =
                            Modifier
                                .padding(top = 100.dp)
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                        contentScale = ContentScale.Crop,
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Name
                    OtakuTitle(
                        title = user.name ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Stats Bar (Anime, Manga, Following, Followers)
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    StatItem(
                        count =
                            user.statistics.anime.count
                                .toString(),
                        labelRes = R.string.anime_,
                    )

                    StatsDivider()

                    StatItem(
                        count =
                            user.statistics.manga.count
                                .toString(),
                        labelRes = R.string.manga_,
                    )

                    StatsDivider()

                    StatItem(count = user.followingCount.toString(), labelRes = R.string.following)

                    StatsDivider()

                    StatItem(count = user.followerCount.toString(), labelRes = R.string.followers)
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Bio Section
                if (user.about.isNotBlank()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OtakuTitle(id = R.string.bio)
                        Spacer(modifier = Modifier.height(10.dp))
                        OtakuTitle(
                            title = user.about,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }

                // Detailed Stats Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OtakuTitle(id = R.string.stats)
                    Spacer(modifier = Modifier.height(15.dp))

                    TagItem(
                        key = R.string.episodes_watched,
                        value =
                            user.statistics.anime.episodesWatched
                                .toString(),
                    )
                    TagItem(key = R.string.days_watched, value = String.format("%.1f", user.statistics.anime.minutesWatched / 1440.0))
                    TagItem(
                        key = R.string.anime_mean_score,
                        value =
                            user.statistics.anime.meanScore
                                .toString(),
                    )
                    TagItem(
                        key = R.string.chapters_read,
                        value =
                            user.statistics.manga.chaptersRead
                                .toString(),
                    )
                    TagItem(
                        key = R.string.volumes_read,
                        value =
                            user.statistics.manga.volumesRead
                                .toString(),
                    )
                    TagItem(
                        key = R.string.manga_mean_score,
                        value =
                            user.statistics.manga.meanScore
                                .toString(),
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Favourites Section
                FavouriteMediaList(
                    titleId = R.string.favourite_anime,
                    mediaList =
                        user.favourites.anime.nodes,
                    mediaType = MediaType.ANIME,
                    onMediaClick = { id ->
                        navActionManager.toMediaDetail(id = id, mediaType = MediaType.ANIME)
                    },
                    onExpandClick = {
                        // todo: navigate to media list screen
                        showFutureToast()
                    },
                )

                FavouriteMediaList(
                    titleId = R.string.favourite_manga,
                    mediaList =
                        user.favourites.manga.nodes,
                    mediaType = MediaType.MANGA,
                    onMediaClick = { id ->
                        navActionManager.toMediaDetail(id = id, mediaType = MediaType.MANGA)
                    },
                    onExpandClick = {
                        // todo: navigate to media list screen
                        showFutureToast()
                    },
                )

                FavouriteCharacterList(
                    titleId = R.string.favourite_characters,
                    characters =
                        user.favourites.characters.nodes,
                    onCharacterClick = { id ->
                        // todo: navigate to character detail
                        showFutureToast()
                    },
                    onExpandClick = {
                        // todo: navigate to character list screen
                        showFutureToast()
                    },
                )

                FavouriteStaffList(
                    titleId = R.string.favourite_staff,
                    staffs =
                        user.favourites.staff.nodes,
                    onStaffClick = { id ->
                        // todo: navigate to staff detail
                        showFutureToast()
                    },
                    onExpandClick = {
                        // todo: navigate to character list screen
                        showFutureToast()
                    },
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
