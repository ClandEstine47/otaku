package com.example.feature.anime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.R

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    showBottomBar: Boolean = true,
    score: Double? = null,
    showScore: Boolean = true,
    isAnime: Boolean,
    totalChapters: Int? = null,
    totalEpisodes: Int? = null,
    releasedEpisodes: Int? = null,
    format: String? = null,
) {
    Column(
        modifier =
            modifier
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = "poster",
                    modifier =
                        Modifier
                            .fillMaxSize(),
                )

                if (showScore) {
                    score?.let {
                        Box(
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(bottomStart = 20.dp))
                                    .padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 2.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = score.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.background,
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    modifier = Modifier.size(15.dp),
                                    tint = MaterialTheme.colorScheme.background,
                                    contentDescription = "score",
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showBottomBar) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(23.dp)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row {
                        releasedEpisodes?.let {
                            Box(
                                modifier =
                                    Modifier
                                        .padding(start = 3.dp, top = 1.dp, bottom = 1.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            ) {
                                Text(
                                    text = releasedEpisodes.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(2.dp),
                                )
                            }
                        }

                        Box(
                            modifier =
                                Modifier
                                    .padding(start = 3.dp, top = 1.dp, bottom = 1.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                        ) {
                            Text(
                                text = ((if (isAnime) totalEpisodes else totalChapters) ?: "?").toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(2.dp),
                            )
                        }
                    }

                    Text(
                        text = format ?: "-",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ImageCardPreview() {
    ImageCard(
        painter = painterResource(id = R.drawable.anime_cover_preview),
        score = 8.9,
        isAnime = true,
        totalChapters = 190,
        totalEpisodes = 12,
        releasedEpisodes = 4,
        format = "MOVIE",
    )
}
