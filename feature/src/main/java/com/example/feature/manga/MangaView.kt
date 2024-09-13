package com.example.feature.manga

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.MediaListContentType
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.anime.ExpandMediaListButton
import com.example.feature.anime.ImageCard
import com.example.feature.anime.InfiniteHorizontalPager
import com.example.feature.anime.OtakuImageCardTitle
import com.example.feature.anime.OtakuTitle
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze

@Composable
fun MangaView(
    navActionManager: NavActionManager,
    hazeState: HazeState,
    mangaViewModel: MangaViewModel = hiltViewModel(),
) {
    val uiState by mangaViewModel.state.collectAsStateWithLifecycle()

    /**
     * Haze blur effect working only for API 32+
     */
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
                )
                .fillMaxSize()
                .absolutePadding()
                .verticalScroll(rememberScrollState()),
    ) {
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
            MangaContent(
                navActionManager = navActionManager,
                trendingMangaList = uiState.trendingMangaList,
                popularMangaList = uiState.popularMangaList,
                popularManhwaList = uiState.popularManhwaList,
                popularNovelList = uiState.popularNovelList,
                popularOneShotList = uiState.popularOneShotList,
            )
        }
    }
}

@Composable
fun MangaContent(
    navActionManager: NavActionManager,
    trendingMangaList: List<Media>? = null,
    popularMangaList: List<Media>? = null,
    popularManhwaList: List<Media>? = null,
    popularNovelList: List<Media>? = null,
    popularOneShotList: List<Media>? = null,
) {
    if (trendingMangaList != null) {
        InfiniteHorizontalPager(mediaList = trendingMangaList)
    }

    Spacer(modifier = Modifier.height(40.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(
            id = R.string.popular_manga,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_manga,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_MANGA,
                )
            },
        )
    }

    popularMangaList?.let { mangas ->
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(mangas) { manga ->
                val painter =
                    rememberAsyncImagePainter(
                        model = manga.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    ImageCard(
                        painter = painter,
                        score = (manga.meanScore.toDouble()) / 10,
                        isAnime = false,
                        totalChapters = manga.chapters,
                        format = manga.format?.name,
                    )

                    OtakuImageCardTitle(title = manga.title.english.ifBlank { manga.title.romaji })
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(
            id = R.string.popular_manhwa,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_manhwa,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_MANHWA,
                )
            },
        )
    }

    popularManhwaList?.let { manhwas ->
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(manhwas) { manhwa ->
                val painter =
                    rememberAsyncImagePainter(
                        model = manhwa.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    ImageCard(
                        painter = painter,
                        score = (manhwa.meanScore.toDouble()) / 10,
                        isAnime = false,
                        totalChapters = manhwa.chapters,
                        format = manhwa.format?.name,
                    )

                    OtakuImageCardTitle(title = manhwa.title.english.ifBlank { manhwa.title.romaji })
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(
            id = R.string.popular_novel,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_novel,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.POPULAR_NOVEL,
                )
            },
        )
    }

    popularNovelList?.let { novels ->
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(novels) { novel ->
                val painter =
                    rememberAsyncImagePainter(
                        model = novel.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    ImageCard(
                        painter = painter,
                        score = (novel.meanScore.toDouble()) / 10,
                        isAnime = false,
                        totalChapters = novel.chapters,
                        format = novel.format?.name,
                    )

                    OtakuImageCardTitle(title = novel.title.english.ifBlank { novel.title.romaji })
                }
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(
            id = R.string.popular_one_shot,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                navActionManager.toMediaList(
                    titleId = R.string.popular_one_shot,
                    mediaType = MediaType.MANGA,
                    contentType = MediaListContentType.ONE_SHOT,
                )
            },
        )
    }

    popularOneShotList?.let { oneShorts ->
        LazyRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(oneShorts) { oneShort ->
                val painter =
                    rememberAsyncImagePainter(
                        model = oneShort.coverImage.large,
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    ImageCard(
                        painter = painter,
                        score = (oneShort.meanScore.toDouble()) / 10,
                        isAnime = false,
                        totalChapters = oneShort.chapters,
                        format = oneShort.format?.name,
                    )

                    OtakuImageCardTitle(title = oneShort.title.english.ifBlank { oneShort.title.romaji })
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(100.dp))
}
