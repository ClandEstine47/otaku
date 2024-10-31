package com.example.feature.screens.mediadetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.ScoreDistribution
import com.example.feature.R
import com.example.feature.common.OtakuTitle

@Composable
fun MediaScoreDistribution(
    score: List<ScoreDistribution>,
) {
    OtakuTitle(id = R.string.score_distribution)
    Column(
        modifier =
            Modifier
                .padding(horizontal = 30.dp)
                .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val floatValue = mutableListOf<Float>()
        // Extract list of scores
        val scores: List<Int> = score.map { it.score ?: 0 }

        // Extract list of amounts
        val amounts: List<Int> = score.map { it.amount ?: 0 }

        amounts.forEachIndexed { index, value ->
            floatValue.add(index = index, element = value.toFloat() / amounts.max().toFloat())
        }

        ScoreDistributionChart(
            graphBarData = floatValue,
            xAxisScaleData = scores,
            barData_ = amounts,
            height = 300.dp,
            roundType = BarType.TOP_CURVED,
            barWidth = 12.dp,
            barColor = MaterialTheme.colorScheme.primary,
            barArrangement = Arrangement.SpaceEvenly,
        )
    }
}
