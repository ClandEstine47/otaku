package com.example.feature.screens.home

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.feature.common.BannerCard
import com.example.feature.common.ErrorScreen
import com.example.feature.common.MediaItem
import com.example.feature.common.OtakuTitle
import com.example.feature.common.TitleWithExpandButton
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel as hiltComposeViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    val pullToRefreshState = rememberPullToRefreshState()
    val hasContent =
        uiState.user.id > 0 ||
            uiState.currentAnimeMedia != null ||
            uiState.currentMangaMedia != null

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && !hasContent && !uiState.isLoading) {
            homeViewModel.loadHomeData()
        }
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
                ).fillMaxSize(),
    ) {
        if (isOnline) {
            if (isLoggedIn) {
                PullToRefreshBox(
                    modifier = Modifier.fillMaxSize(),
                    isRefreshing = uiState.isLoading && hasContent,
                    onRefresh = {
                        homeViewModel.loadHomeData()
                    },
                    state = pullToRefreshState,
                ) {
                    if (uiState.isLoading && !hasContent) {
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
                    } else if (uiState.error == null) {
                        HomeContent(
                            navActionManager = navActionManager,
                            user = uiState.user,
                            currentAnimeMedia = uiState.currentAnimeMedia,
                            currentMangaMedia = uiState.currentMangaMedia,
                            onLogoutClick = {
                                homeViewModel.logout()
                            },
                        )
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
                } else if (uiState.error == null) {
                    AuthContent()
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
    user: User,
    currentAnimeMedia: List<Media>? = null,
    currentMangaMedia: List<Media>? = null,
    onLogoutClick: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val bannerVisible = user.bannerImage.isNotBlank()
    val bannerPainter =
        if (bannerVisible) {
            rememberAsyncImagePainter(model = user.bannerImage)
        } else {
            null
        }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
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
                                .size(50.dp)
                                .clip(RoundedCornerShape(50.dp)),
                    )

                    OtakuTitle(
                        title = user.name ?: "-",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = {
                                // todo: navigate to settings
                            })
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.settings),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Settings",
                    )

                    OtakuTitle(
                        title = stringResource(R.string.settings),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = {
                                showLogoutDialog = true
                            })
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.logout),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Logout",
                    )

                    OtakuTitle(
                        title = stringResource(R.string.logout),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                OtakuTitle(
                    title = stringResource(R.string.logout),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                OtakuTitle(
                    title = stringResource(R.string.logout_confirmation),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    },
                ) {
                    OtakuTitle(
                        title = stringResource(R.string.no),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onLogoutClick()
                        showLogoutDialog = false
                        showBottomSheet = false
                    },
                ) {
                    OtakuTitle(
                        title = stringResource(R.string.yes),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
        )
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .absolutePadding(),
        ) {
            if (bannerVisible && bannerPainter != null) {
                BannerCard(
                    bannerPainter = bannerPainter,
                    height = 232.dp,
                )
            }
            Column(
                modifier =
                    Modifier
                        .padding(20.dp)
                        .padding(top = 30.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
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
                                    .clip(RoundedCornerShape(50.dp))
                                    .clickable(onClick = {
                                        showBottomSheet = true
                                    }),
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                StatBadge(
                                    count =
                                        user.statistics.anime.count
                                            .toString(),
                                    label = stringResource(R.string.anime_),
                                )

                                StatBadge(
                                    count =
                                        user.statistics.manga.count
                                            .toString(),
                                    label = stringResource(R.string.manga_),
                                )
                            }
                        }
                    }

                    Surface(
                        modifier =
                            Modifier
                                .size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.16f),
                        shadowElevation = 0.dp,
                        border =
                            androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                brush =
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors =
                                            listOf(
                                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f),
                                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
                                            ),
                                    ),
                            ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            val count = user.unreadNotificationCount

                            IconButton(
                                modifier = Modifier.fillMaxSize(),
                                onClick = {
                                    // todo: navigate to notifications
                                },
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (count > 0) {
                                            Badge(containerColor = Color.Red) {
                                                Text(
                                                    text = if (count > 99) "99+" else count.toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            }
                                        }
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(22.dp),
                                        painter =
                                            painterResource(
                                                if (count > 0) {
                                                    R.drawable.notification_active
                                                } else {
                                                    R.drawable.notification
                                                },
                                            ),
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        contentDescription = "notifications",
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
                ) {
                    HomeListTile(
                        imageRes = R.drawable.anime_list,
                        title = "ANIME LIST",
                        onClick = {
                            navActionManager.toUserCurrentAnimeList(
                                titleId = R.string.anime,
                                userId = user.id.takeIf { it > 0 },
                                showStatusTabs = true,
                            )
                        },
                    )

                    HomeListTile(
                        imageRes = R.drawable.manga_list,
                        title = "MANGA LIST",
                        onClick = {
                            navActionManager.toUserCurrentMangaList(
                                titleId = R.string.manga,
                                userId = user.id.takeIf { it > 0 },
                                showStatusTabs = true,
                            )
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            currentAnimeMedia?.takeIf { it.isNotEmpty() }?.let { currentAnime ->
                TitleWithExpandButton(
                    titleId = R.string.current_anime,
                    onExpandClick = {
                        navActionManager.toUserCurrentAnimeList(
                            titleId = R.string.current_anime,
                            userId = user.id.takeIf { it > 0 },
                        )
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
                        navActionManager.toUserCurrentMangaList(
                            titleId = R.string.current_manga,
                            userId = user.id.takeIf { it > 0 },
                        )
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

            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun RowScope.HomeListTile(
    imageRes: Int,
    title: String,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            Modifier
                .weight(1f)
                .height(70.dp)
                .clip(RoundedCornerShape(15.dp))
                .clickable(onClick = onClick),
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
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OtakuTitle(
                    title = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier =
                        Modifier
                            .width(56.dp)
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.primary),
                )
            }
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

@Composable
fun StatBadge(
    count: String,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier =
                Modifier
                    .height(32.dp)
                    .defaultMinSize(minWidth = 50.dp)
                    .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                OtakuTitle(
                    title = count,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OtakuTitle(
            title = label,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
        )
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
