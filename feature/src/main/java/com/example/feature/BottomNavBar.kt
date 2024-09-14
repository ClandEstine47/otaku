package com.example.feature

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@Composable
fun BottomNavBar(
    navBarItems: List<NavBarItem>,
    hazeState: HazeState,
    navigate: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val animatedSelectedTabIndex by animateFloatAsState(
        targetValue = selectedTabIndex.toFloat(),
        label = "animatedSelectedTabIndex",
        animationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy,
            ),
    )

    val animatedColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        label = "animatedColor",
        animationSpec =
            spring(
                stiffness = Spring.StiffnessLow,
            ),
    )

    Box(
        modifier =
            Modifier
                .padding(vertical = 24.dp, horizontal = 64.dp)
                .fillMaxWidth()
                .height(64.dp)
                .hazeChild(state = hazeState, shape = CircleShape)
                .border(
                    width = Dp.Hairline,
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = .8f),
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = .2f),
                                ),
                        ),
                    shape = CircleShape,
                ),
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides
                LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                ),
            LocalContentColor provides MaterialTheme.colorScheme.onBackground,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                navBarItems.forEachIndexed { index, navBarItem ->
                    val alpha by animateFloatAsState(
                        targetValue = if (selectedTabIndex == index) 1f else .35f,
                        label = "alpha",
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (selectedTabIndex == index) 1f else .98f,
                        visibilityThreshold = .000001f,
                        animationSpec =
                            spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                            ),
                        label = "scale",
                    )
                    Column(
                        modifier =
                            Modifier
                                .scale(scale)
                                .alpha(alpha)
                                .fillMaxHeight()
                                .weight(1f)
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        selectedTabIndex = index
                                        navigate(navBarItem.title)
                                    }
                                },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    ) {
                        Icon(
                            painter = painterResource(id = if (selectedTabIndex == index) navBarItem.iconEnabled else navBarItem.iconDisabled),
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = "NavBar Icon",
                        )
                        Text(
                            text = navBarItem.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
        }

        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
        ) {
            val tabWidth = size.width / navBarItems.size
            drawCircle(
                color = animatedColor.copy(alpha = .6f),
                radius = size.height / 2,
                center =
                    Offset(
                        (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                        size.height / 2,
                    ),
            )
        }

        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
        ) {
            val path =
                Path().apply {
                    addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height)))
                }
            val length = PathMeasure().apply { setPath(path, false) }.length

            val tabWidth = size.width / navBarItems.size
            drawPath(
                path,
                brush =
                    Brush.horizontalGradient(
                        colors =
                            listOf(
                                animatedColor.copy(alpha = 0f),
                                animatedColor.copy(alpha = 1f),
                                animatedColor.copy(alpha = 1f),
                                animatedColor.copy(alpha = 0f),
                            ),
                        startX = tabWidth * animatedSelectedTabIndex,
                        endX = tabWidth * (animatedSelectedTabIndex + 1),
                    ),
                style =
                    Stroke(
                        width = 6f,
                        pathEffect =
                            PathEffect.dashPathEffect(
                                intervals = floatArrayOf(length / 2, length),
                            ),
                    ),
            )
        }
    }
}

data class NavBarItem(
    val title: String,
    val iconEnabled: Int,
    val iconDisabled: Int,
)
