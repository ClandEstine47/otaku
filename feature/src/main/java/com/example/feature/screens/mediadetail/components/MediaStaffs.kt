package com.example.feature.screens.mediadetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.staff.StaffConnection
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.common.ExpandMediaListButton
import com.example.feature.common.ImageCard
import com.example.feature.common.OtakuImageCardTitle
import com.example.feature.common.OtakuTitle

@Composable
fun MediaStaffs(
    staffs: StaffConnection,
    navActionManager: NavActionManager,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(id = R.string.staffs)
        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                // todo: navigate to Staff list view
            },
        )
    }

    LazyRow(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        staffs.edges?.let { staffs ->
            items(staffs) { staff ->
                val painter =
                    rememberAsyncImagePainter(
                        model = staff.node.image.medium?.ifBlank { staff.node.image.large },
                    )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier =
                        Modifier
                            .clickable {
                                // todo: navigate to Staff Detail
                            },
                ) {
                    ImageCard(
                        painter = painter,
                        isAnime = true,
                        showScore = false,
                        showBottomBar = false,
                    )

                    OtakuImageCardTitle(
                        title =
                            staff.node.name.full
                                ?: "",
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    staff.role?.let { role ->
                        OtakuTitle(
                            modifier = Modifier.width(100.dp),
                            title = role.uppercase(),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
