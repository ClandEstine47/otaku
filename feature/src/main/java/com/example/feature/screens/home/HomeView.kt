package com.example.feature.screens.home

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.data.service.isOnline
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.user.User
import com.example.core.navigation.NavActionManager
import com.example.feature.OTAKU_AUTH_URL
import com.example.feature.R
import com.example.feature.Utils.openActionView
import com.example.feature.common.ErrorScreen
import com.example.feature.common.MediaItem
import com.example.feature.common.OtakuTitle
import com.example.feature.common.TitleWithExpandButton
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel as hiltComposeViewModel

@Composable
fun HomeView(
    navActionManager: NavActionManager,
    isLoggedIn: Boolean,
    hazeState: HazeState,
    homeViewModel: HomeViewModel = hiltComposeViewModel(),
) {
    val uiState by homeViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isOnline by remember {
        mutableStateOf(isOnline(context))
    }

    // Haze blur effect working only for API 32+
    Column(
        modifier =
            Modifier
                .haze(
                    hazeState,
                    HazeStyle(
                        tint = MaterialTheme.colorScheme.background.copy(alpha = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) .2f else .8f),
                        blurRadius = 30.dp,
                        noiseFactor = HazeDefaults.noiseFactor,
                    ),
                ).fillMaxSize()
                .absolutePadding(),
    ) {
        if (isOnline) {
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
                if (uiState.error == null) {
                    if (isLoggedIn) {
                        HomeContent(
                            navActionManager = navActionManager,
                            user = uiState.user,
                            currentAnimeMedia = uiState.currentAnimeMedia,
                            currentMangaMedia = uiState.currentMangaMedia,
                        )
                    } else {
                        AuthContent()
                    }
                } else {
                    ErrorScreen(
                        modifier = Modifier.padding(top = 350.dp),
                        onRetryClick = {
                            isOnline = isOnline(context)
                            homeViewModel.loadHomeData()
                        },
                    )
                }
            }
        } else {
            ErrorScreen(
                modifier = Modifier.padding(top = 350.dp),
                errorMessage = stringResource(R.string.no_internet),
                onRetryClick = {
                    isOnline = isOnline(context)
                    homeViewModel.loadHomeData()
                },
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
    user: User,
    currentAnimeMedia: List<Media>? = null,
    currentMangaMedia: List<Media>? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(top = 30.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter =
                    rememberAsyncImagePainter(
                        model = user.avatar.medium,
                    ),
                contentDescription = "profile picture",
                modifier =
                    Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(30.dp)),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OtakuTitle(
                    title = user.name ?: "-",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                )

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column {
                        OtakuTitle(
                            title =
                                user.statistics.anime.count
                                    .toString(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        OtakuTitle(
                            title = stringResource(R.string.anime),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Column {
                        OtakuTitle(
                            title =
                                user.statistics.manga.count
                                    .toString(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        OtakuTitle(
                            title = stringResource(R.string.manga),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        ) {
            HomeListTile(
                imageRes = R.drawable.anime_banner_preview,
                title = "ANIME LIST",
            )

            HomeListTile(
                imageRes = R.drawable.anime_cover_preview,
                title = "MANGA LIST",
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        currentAnimeMedia?.takeIf { it.isNotEmpty() }?.let { currentAnime ->
            TitleWithExpandButton(
                titleId = R.string.current_anime,
                onExpandClick = {
                    // todo: browse users current anime lists
                },
            )

            LazyRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(currentAnime) { anime ->
                    MediaItem(
                        media = anime,
                        isAnime = true,
                        releasedEpisodes = anime.nextAiringEpisode?.episode?.minus(1),
                        progressCount = anime.mediaListEntry?.progress,
                        showProgress = true,
                        onClick = { id ->
                            navActionManager.toMediaDetail(
                                id = id,
                                mediaType = MediaType.ANIME,
                            )
                        },
                    )
                }
            }
        }

        currentMangaMedia?.takeIf { it.isNotEmpty() }?.let { currentManga ->
            TitleWithExpandButton(
                titleId = R.string.current_manga,
                onExpandClick = {
                    // todo: browse users current manga lists
                },
            )

            LazyRow(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(currentManga) { manga ->
                    MediaItem(
                        media = manga,
                        isAnime = false,
                        progressCount = manga.mediaListEntry?.progress,
                        showProgress = true,
                        onClick = { id ->
                            navActionManager.toMediaDetail(
                                id = id,
                                mediaType = MediaType.MANGA,
                            )
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun RowScope.HomeListTile(
    imageRes: Int,
    title: String,
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .height(70.dp)
                .clip(RoundedCornerShape(15.dp)),
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center,
        ) {
            OtakuTitle(
                title = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun AuthContent() {
    val context = LocalContext.current
    val githubUrl = stringResource(R.string.github_url)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OtakuTitle(
            title = stringResource(R.string.otaku),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 60.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OtakuTitle(
            id = R.string.powered_by_anilist,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(75.dp))

        IconButton(
            onClick = {
                context.openActionView(githubUrl)
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.github),
                contentDescription = "Github Link",
            )
        }

        Spacer(modifier = Modifier.height(75.dp))

        Button(
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp),
            onClick = {
                context.openActionView(OTAKU_AUTH_URL)
            },
        ) {
            OtakuTitle(
                id = R.string.login,
                color = MaterialTheme.colorScheme.background,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun AuthContentPreview(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .absolutePadding(),
    ) {
        AuthContent()
    }
}
