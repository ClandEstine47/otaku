package com.example.feature.anime

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.core.domain.model.media.Media
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfiniteHorizontalPager(mediaList: List<Media>) {
    val pagerState = rememberPagerState { Int.MAX_VALUE }

    // infinite scroll
    LaunchedEffect(key1 = Unit) {
        var initPage = Int.MAX_VALUE / 2
        while (initPage % mediaList.size != 0) {
            initPage++
        }
        pagerState.scrollToPage(initPage)
    }

    // auto scroll
    LaunchedEffect(key1 = pagerState.currentPage) {
        launch {
            while (true) {
                delay(5000L)

                withContext(NonCancellable) {
                    if (pagerState.currentPage + 1 in 0..Int.MAX_VALUE) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        val initPage = Int.MAX_VALUE / 2
                        pagerState.scrollToPage(initPage)
                    }
                }
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
            ) { index ->
                mediaList.getOrNull(
                    index % (mediaList.size),
                )?.let { media ->
                    BannerItem(media = media)
                }
            }
        }
    }
}

@Composable
fun BannerItem(media: Media) {
    val bannerPainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(
                        media.bannerImage.ifBlank {
                            media.coverImage.extraLarge
                        },
                    )
                    .crossfade(true)
                    .build(),
        )

    val coverPainter =
        rememberAsyncImagePainter(
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(media.coverImage.large)
                    .crossfade(true)
                    .build(),
        )

    Box(modifier = Modifier.fillMaxSize()) {
        BannerCard(
            bannerPainter = bannerPainter,
            coverPainter = coverPainter,
            score = (media.meanScore.toDouble() / 10),
        )

        Column(
            modifier = Modifier.padding(start = 20.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ImageCard(
                        modifier = Modifier,
                        painter = coverPainter,
                        score = (media.meanScore.toDouble()) / 10,
                        showScore = true,
                        showBottomBar = false,
                        isAnime = true,
                    )
                }

                Column(
                    modifier =
                        Modifier
                            .height(IntrinsicSize.Max)
                            .align(Alignment.Bottom),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    OtakuTitle(
                        title = media.title.romaji,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OtakuTitle(
                        title = media.status?.name ?: "-",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                media.genres?.let { genres ->
                    val genreCount = genres.size
                    genres.forEachIndexed { index, genre ->
                        genre?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (index < genreCount - 1) {
                                Text(
                                    text = " \u00B7 ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HtmlViewer(html: String) {
    val richTextState = rememberRichTextState()

    // Toggle a span style.
    richTextState.toggleSpanStyle(
        SpanStyle(
            fontWeight = FontWeight.Normal,
        ),
    )
    richTextState.setHtml(html)

    RichTextEditor(
        state = richTextState,
        readOnly = true,
        maxLines = 5,
        contentPadding = PaddingValues(0.dp),
        colors =
            RichTextEditorDefaults.richTextEditorColors(
                containerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
    )
}
