package com.example.feature.mediadetail

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
) {
    val annotatedText =
        remember(html) {
            val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
            buildAnnotatedString {
                append(spanned)
            }
        }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Light,
        textAlign = TextAlign.Justify,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandableHtmlText(
    html: String,
    maxLines: Int = 5,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val gradientAlpha by animateFloatAsState(
        targetValue = if (expanded) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "description animation",
    )

    Column(
        modifier =
            Modifier
                .padding(end = 5.dp)
                .animateContentSize(
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                ),
    ) {
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
            HtmlText(
                html = html,
                modifier =
                    Modifier
                        .heightIn(max = if (expanded) Dp.Unspecified else (maxLines * 24).dp),
            )
            if (!expanded) {
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(100.dp)
                        .alpha(gradientAlpha)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                            ),
                        ),
                )
            }
        }

        IconButton(
            onClick = {
                expanded = !expanded
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                modifier = Modifier.size(30.dp),
                contentDescription = "expand/ collapse",
            )
        }
    }
}
