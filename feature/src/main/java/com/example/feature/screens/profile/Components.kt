package com.example.feature.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.character.Character
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaType
import com.example.core.domain.model.staff.Staff
import com.example.feature.common.ImageCard
import com.example.feature.common.MediaItem
import com.example.feature.common.OtakuImageCardTitle
import com.example.feature.common.OtakuTitle
import com.example.feature.common.TitleWithExpandButton

@Composable
fun StatItem(
    count: String,
    labelRes: Int,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OtakuTitle(
            title = count,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        OtakuTitle(
            id = labelRes,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        )
    }
}

@Composable
fun StatsDivider(modifier: Modifier = Modifier) {
    Divider(
        modifier =
            Modifier
                .height(40.dp)
                .width(1.dp)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)),
    )
}

@Composable
fun FavouriteMediaList(
    titleId: Int,
    mediaList: List<Media>?,
    mediaType: MediaType,
    onMediaClick: (Int) -> Unit,
    onExpandClick: () -> Unit = {},
) {
    if (!mediaList.isNullOrEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            TitleWithExpandButton(
                titleId = titleId,
                titleStartPadding = 0.dp,
                onExpandClick = onExpandClick,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(mediaList) { media ->
                    MediaItem(
                        media = media,
                        isAnime = mediaType == MediaType.ANIME,
                        onClick = { id ->
                            onMediaClick(id)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteCharacterList(
    titleId: Int,
    characters: List<Character>?,
    onCharacterClick: (Int) -> Unit,
    onExpandClick: () -> Unit = {},
) {
    if (!characters.isNullOrEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TitleWithExpandButton(
                titleId = titleId,
                titleStartPadding = 0.dp,
                onExpandClick = onExpandClick,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(characters) { character ->
                    val painter =
                        rememberAsyncImagePainter(
                            model = character.image?.medium ?: character.image?.large,
                        )
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier =
                            Modifier.clickable {
                                onCharacterClick(character.id!!)
                            },
                    ) {
                        ImageCard(
                            painter = painter,
                            isAnime = true,
                            showScore = false,
                            showBottomBar = false,
                        )
                        OtakuImageCardTitle(title = character.name?.full ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun FavouriteStaffList(
    titleId: Int,
    staffs: List<Staff>?,
    onStaffClick: (Int) -> Unit,
    onExpandClick: () -> Unit = {},
) {
    if (!staffs.isNullOrEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TitleWithExpandButton(
                titleId = titleId,
                titleStartPadding = 0.dp,
                onExpandClick = onExpandClick,
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(staffs) { staff ->
                    val painter =
                        rememberAsyncImagePainter(model = staff.image.medium ?: staff.image.large)
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier =
                            Modifier.clickable {
                                onStaffClick(staff.id!!)
                            },
                    ) {
                        ImageCard(
                            painter = painter,
                            isAnime = true,
                            showScore = false,
                            showBottomBar = false,
                        )
                        OtakuImageCardTitle(title = staff.name.full ?: "")
                    }
                }
            }
        }
    }
}
