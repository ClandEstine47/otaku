package com.example.feature.screens.mediadetail.components

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.feature.common.OtakuTitle

@Composable
fun StatusDistributionChart(
    data: Map<String, Int>,
    colors: List<Color>,
    radiusOuter: Dp = 140.dp,
    chartBarWidth: Dp = 70.dp,
    animDuration: Int = 2000,
) {
    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    var animationPlayed by remember { mutableStateOf(false) }

    var lastValue = 0f

    // it is the diameter value of the Pie
    val animateSize by animateFloatAsState(
        targetValue = if (animationPlayed) radiusOuter.value * 2f else 0f,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = LinearOutSlowInEasing,
            ),
    )

    val animateRotation by animateFloatAsState(
        targetValue = if (animationPlayed) 90f * 11f else 0f,
        animationSpec =
            tween(
                durationMillis = animDuration,
                delayMillis = 0,
                easing = LinearOutSlowInEasing,
            ),
    )

    // to play the animation only once when the function is Created or Recomposed
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
    ) {
        // Pie Chart using Canvas Arc
        Box(
            modifier =
                Modifier
                    .size(animateSize.dp)
                    .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(
                modifier =
                    Modifier
                        .offset { IntOffset.Zero }
                        .size(radiusOuter * 2f)
                        .rotate(animateRotation),
            ) {
                // draw each Arc for each data entry in Pie Chart
                floatValue.forEachIndexed { index, value ->
                    drawArc(
                        color = colors[index],
                        lastValue,
                        value,
                        useCenter = false,
                        style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt),
                    )
                    lastValue += value
                }
            }
        }
    }
}

@Composable
fun StatusDistributionDetails(
    data: Map<String, Int>,
    colors: List<Color>,
) {
    Column(
        modifier =
        Modifier,
    ) {
        // create the data items
        data.values.forEachIndexed { index, value ->
            StatusDistributionDetail(
                data = Pair(data.keys.elementAt(index), value),
                color = colors[index],
            )
        }
    }
}

@Composable
fun StatusDistributionDetail(
    data: Pair<String, Int>,
    height: Dp = 45.dp,
    color: Color,
) {
    Surface(
        modifier =
            Modifier
                .padding(vertical = 10.dp),
        color = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .background(
                            color = color,
                            shape = RoundedCornerShape(50.dp),
                        )
                        .size(height),
            )

            Column {
                StatusDistributionDetailItem(
                    title = data.first,
                    body = data.second.toString(),
                )
            }
        }
    }
}

@Composable
fun StatusDistributionDetailItem(
    title: String,
    body: String,
) {
    Column {
        OtakuTitle(
            title = title,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
        )
        OtakuTitle(
            title = body,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
