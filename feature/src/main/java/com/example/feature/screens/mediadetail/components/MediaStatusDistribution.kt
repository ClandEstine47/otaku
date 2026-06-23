package com.example.feature.screens.mediadetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.StatusDistribution
import com.example.feature.R
import com.example.feature.common.OtakuTitle

@Composable
fun MediaStatusDistribution(
    status: List<StatusDistribution>,
    averageScore: Int,
    meanScore: Int,
    popularity: Int,
    favourites: Int,
) {
    val chartData =
        status.associate {
            Pair(it.status?.name ?: "Unknown", it.amount ?: 0)
        }

    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
        )

    OtakuTitle(id = R.string.status_distribution)
    StatusDistributionChart(
        data = chartData,
        colors = colors,
    )

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        StatusDistributionDetails(
            data = chartData,
            colors = colors,
        )

        VerticalDivider(
            modifier =
                Modifier
                    .height(300.dp),
            thickness = 0.2.dp,
            color = MaterialTheme.colorScheme.onBackground,
        )

        MediaPerformance(
            averageScore = averageScore,
            meanScore = meanScore,
            popularity = popularity,
            favourites = favourites,
        )
    }
}
