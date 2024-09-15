package com.example.feature.common

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaRankType
import com.example.feature.R
import com.example.feature.anime.BannerCard
import com.example.feature.anime.ImageCard
import com.example.feature.anime.OtakuTitle
import com.example.feature.mediadetail.ExpandableHtmlText

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun BannerItem(
    media: Media,
    rankingVisibility: Boolean = false,
    descriptionVisibility: Boolean = false,
    onBannerItemClick: (mediaId: Int) -> Unit,
) {
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
                        .padding(top = 150.dp)
                        .combinedClickable(
                            onClick = {
                                onBannerItemClick(media.idAniList)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ),
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
                        title = media.title.english.ifBlank { media.title.romaji },
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OtakuTitle(
                        title = media.status?.stringRes?.let { stringResource(id = it) } ?: "-",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }

            if (rankingVisibility) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.Start),
                ) {
                    val highestRatedRanking = media.rankings?.firstOrNull { ranking -> ranking.allTime == true && ranking.type == MediaRankType.RATED }?.rank ?: 0
                    val mostPopularRanking = media.rankings?.firstOrNull { ranking -> ranking.allTime == true && ranking.type == MediaRankType.POPULAR }?.rank ?: 0

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Highest rated all time",
                        )

                        OtakuTitle(
                            title = "#$highestRatedRanking",
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.flame),
                            contentDescription = "Most popular all time",
                        )

                        OtakuTitle(
                            title = "#$mostPopularRanking",
                            fontWeight = FontWeight.Light,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
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
            if (descriptionVisibility) {
                ExpandableHtmlText(html = media.description ?: "")
            }
        }
    }
}
