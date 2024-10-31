package com.example.feature.screens.mediadetail.components

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

enum class BarType {
    CIRCULAR_TYPE,
    TOP_CURVED,
}

@Composable
fun ScoreDistributionChart(
    graphBarData: List<Float>,
    xAxisScaleData: List<Int>,
    barData_: List<Int>,
    height: Dp,
    roundType: BarType,
    barWidth: Dp,
    barColor: Color,
    barArrangement: Arrangement.Horizontal,
) {
    val barData by remember {
        mutableStateOf(barData_ + 0)
    }

    // for getting screen width and height you can use LocalConfiguration
    val configuration = LocalConfiguration.current
    // getting screen width
    val width = configuration.screenWidthDp.dp

    // bottom height of the X-Axis Scale
    val xAxisScaleHeight = 40.dp

    val yAxisScaleSpacing by remember {
        mutableFloatStateOf(100f)
    }
    val yAxisTextWidth by remember {
        mutableStateOf(100.dp)
    }

    // bar shape
    val barShape =
        when (roundType) {
            BarType.CIRCULAR_TYPE -> CircleShape
            BarType.TOP_CURVED -> RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
        }

    val density = LocalDensity.current
    val paintColor = MaterialTheme.colorScheme.onBackground.hashCode()
    val lightPaintColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    // y-axis scale text paint
    val textPaint =
        remember(density) {
            Paint().apply {
                color = paintColor
                textAlign = Paint.Align.CENTER
                textSize = density.run { 12.sp.toPx() }
            }
        }

    // for y coordinates of y-axis scale to create horizontal dotted line indicating y-axis scale
    val yCoordinates = mutableListOf<Float>()
    // for dotted line effect
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    // height of vertical line over x-axis scale connecting x-axis horizontal line
    val lineHeightXAxis = 10.dp
    // height of horizontal line over x-axis scale
    val horizontalLineHeight = 5.dp

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart,
    ) {
        // y-axis scale and horizontal dotted lines on graph indicating y-axis scale
        Column(
            modifier =
                Modifier
                    .padding(top = xAxisScaleHeight, end = 3.dp)
                    .height(height)
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Canvas(modifier = Modifier.padding(bottom = 10.dp).fillMaxSize()) {
                // Y-Axis Scale Text
                val yAxisScaleText = (barData.max()) / 3f
                (0..3).forEach { i ->
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            round(barData.min() + yAxisScaleText * i).toString(),
                            30f,
                            size.height - yAxisScaleSpacing - i * size.height / 3f,
                            textPaint,
                        )
                    }
                    yCoordinates.add(size.height - yAxisScaleSpacing - i * size.height / 3f)
                }

                // horizontal dotted lines on graph indicating y-axis scale
                (1..3).forEach {
                    drawLine(
                        start = Offset(x = yAxisScaleSpacing + 30f, y = yCoordinates[it]),
                        end = Offset(x = size.width, y = yCoordinates[it]),
                        color = lightPaintColor,
                        strokeWidth = 5f,
                        pathEffect = pathEffect,
                    )
                }
            }
        }

        // Graph with Bar Graph and X-Axis Scale
        Box(
            modifier =
                Modifier
                    .padding(start = 50.dp)
                    .width(width - yAxisTextWidth)
                    .height(height + xAxisScaleHeight),
            contentAlignment = BottomCenter,
        ) {
            Row(
                modifier =
                    Modifier
                        .width(width - yAxisTextWidth),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = barArrangement,
            ) {
                // Graph
                graphBarData.forEachIndexed { index, value ->

                    var animationTriggered by remember {
                        mutableStateOf(false)
                    }
                    val graphBarHeight by animateFloatAsState(
                        targetValue = if (animationTriggered) value else 0f,
                        animationSpec =
                            tween(
                                durationMillis = 1000,
                                delayMillis = 0,
                            ),
                    )
                    LaunchedEffect(key1 = true) {
                        animationTriggered = true
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Top,
                        horizontalAlignment = CenterHorizontally,
                    ) {
                        // Each Graph
                        Box(
                            modifier =
                                Modifier
                                    .padding(bottom = 5.dp)
                                    .clip(barShape)
                                    .width(barWidth)
                                    .height(height - 10.dp)
                                    .background(Color.Transparent),
                            contentAlignment = BottomCenter,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .clip(barShape)
                                        .fillMaxWidth()
                                        .fillMaxHeight(graphBarHeight)
                                        .background(barColor),
                            )
                        }

                        // scale x-axis and bottom part of graph
                        Column(
                            modifier =
                                Modifier
                                    .height(xAxisScaleHeight),
                            verticalArrangement = Top,
                            horizontalAlignment = CenterHorizontally,
                        ) {
                            // small vertical line joining the horizontal x-axis line
                            Box(
                                modifier =
                                    Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 2.dp,
                                                bottomEnd = 2.dp,
                                            ),
                                        )
                                        .width(horizontalLineHeight)
                                        .height(lineHeightXAxis)
                                        .background(color = lightPaintColor),
                            )
                            // scale x-axis
                            Text(
                                modifier = Modifier.padding(bottom = 3.dp),
                                text = xAxisScaleData[index].toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            )
                        }
                    }
                }
            }

            // horizontal line on x-axis below the graph
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                horizontalAlignment = CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .padding(bottom = xAxisScaleHeight + 3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .fillMaxWidth()
                            .height(horizontalLineHeight)
                            .background(lightPaintColor),
                )
            }
        }
    }
}
