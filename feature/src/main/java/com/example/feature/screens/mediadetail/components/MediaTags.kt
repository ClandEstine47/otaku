package com.example.feature.screens.mediadetail.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.media.MediaTag
import com.example.feature.common.OtakuTitle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaTags(
    tags: List<MediaTag>,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val gradientAlpha by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "description animation",
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        expanded = !expanded
                    },
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ),
    ) {
        LazyVerticalGrid(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (expanded) 300.dp else 150.dp),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.SpaceBetween,
            userScrollEnabled = expanded,
        ) {
            items(tags) { tag ->
                TagItem(
                    key = tag.name,
                    value = "${tag.rank}%",
                    maxLines = 1,
                    spacing = 5.dp,
                    containsSpoiler = tag.isGeneralSpoiler == true,
                )
            }
        }

        if (!expanded) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(100.dp)
                    .alpha(gradientAlpha)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background,
                                ),
                        ),
                    ),
            )
        }
    }
}

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    maxLines: Int = Int.MAX_VALUE,
    spacing: Dp = 50.dp,
    containsSpoiler: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OtakuTitle(title = key, color = if (containsSpoiler) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontWeight = FontWeight.Light, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.width(spacing))
        OtakuTitle(title = value, textAlign = TextAlign.End, fontWeight = FontWeight.Light, style = MaterialTheme.typography.bodyMedium, maxLines = maxLines, modifier = Modifier.padding(end = spacing))
    }
}
