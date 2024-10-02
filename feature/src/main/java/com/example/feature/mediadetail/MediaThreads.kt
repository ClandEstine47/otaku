package com.example.feature.mediadetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.thread.Thread
import com.example.feature.R
import com.example.feature.Utils
import com.example.feature.anime.ExpandMediaListButton
import com.example.feature.anime.OtakuTitle

@Composable
fun MediaThreads(
    threads: List<Thread>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(id = R.string.threads)

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                // todo: navigate to threads list
            },
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        threads.forEach { thread ->
            MediaThreadItem(thread = thread)
        }
    }
}

@Composable
fun MediaThreadItem(
    thread: Thread,
) {
    val profileImage =
        rememberAsyncImagePainter(
            model = thread.user?.avatar?.medium?.ifBlank { thread.user?.avatar?.large },
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Image(
                    painter = profileImage,
                    contentDescription = "profile image",
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OtakuTitle(title = thread.user?.name ?: "-", color = MaterialTheme.colorScheme.primary)
                        OtakuTitle(
                            title = Utils.secondsToMonthYearDateFormatter(seconds = thread.createdAt ?: 0),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Text(
                        text = thread.title ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Justify,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.visibility),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                contentDescription = "view count",
                            )
                            OtakuTitle(
                                title = (thread.viewCount ?: 0).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.social),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                contentDescription = "reply count",
                            )
                            OtakuTitle(
                                title = (thread.replyCount ?: 0).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}
