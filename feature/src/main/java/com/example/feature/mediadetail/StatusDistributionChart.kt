package com.example.feature.mediadetail

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
import com.example.feature.anime.OtakuTitle

@Composable
fun StatusDistributionChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 140.dp,
    chartBarWidth: Dp = 70.dp,
    animDuration: Int = 2000,
) {
    val pieChartColor1 = Color(0xFF03045e)
    val pieChartColor2 = Color(0xFF0077b6)
    val pieChartColor3 = Color(0xFF00b4d8)
    val pieChartColor4 = Color(0xFF90e0ef)
    val pieChartColor5 = Color(0xFFcaf0f8)

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()

    // To set the value of each Arc according to
    // the value given in the data, we have used a simple formula.
    // For a detailed explanation check out the Medium Article.
    // The link is in the about section and readme file of this GitHub Repository
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }

    // add the colors as per the number of data(no. of pie chart entries)
    // so that each data will get a color
    val colors =
        listOf(
            pieChartColor1,
            pieChartColor2,
            pieChartColor3,
            pieChartColor4,
            pieChartColor5,
        )

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

    // if you want to stabilize the Pie Chart you can use value -90f
    // 90f is used to complete 1/4 of the rotation
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

        // To see the data in more structured way
        // Compose Function in which Items are showing data
        DetailsPieChart(
            data = data,
            colors = colors,
        )
    }
}

@Composable
fun DetailsPieChart(
    data: Map<String, Int>,
    colors: List<Color>,
) {
    Column(
        modifier =
            Modifier
                .padding(top = 80.dp)
                .fillMaxWidth(),
    ) {
        // create the data items
        data.values.forEachIndexed { index, value ->
            DetailsPieChartItem(
                data = Pair(data.keys.elementAt(index), value),
                color = colors[index],
            )
        }
    }
}

@Composable
fun DetailsPieChartItem(
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
            modifier = Modifier.fillMaxWidth(),
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

            Column(modifier = Modifier.fillMaxWidth()) {
                OtakuTitle(
                    title = data.first,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                OtakuTitle(
                    title = data.second.toString(),
                    fontWeight = FontWeight.Light,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
