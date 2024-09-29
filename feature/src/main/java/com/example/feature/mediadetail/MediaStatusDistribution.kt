package com.example.feature.mediadetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.StatusDistribution
import com.example.feature.R
import com.example.feature.anime.OtakuTitle

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

    val pieChartColor1 = Color(0xFF03045e)
    val pieChartColor2 = Color(0xFF0077b6)
    val pieChartColor3 = Color(0xFF00b4d8)
    val pieChartColor4 = Color(0xFF90e0ef)
    val pieChartColor5 = Color(0xFFcaf0f8)

    val colors =
        listOf(
            pieChartColor1,
            pieChartColor2,
            pieChartColor3,
            pieChartColor4,
            pieChartColor5,
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
