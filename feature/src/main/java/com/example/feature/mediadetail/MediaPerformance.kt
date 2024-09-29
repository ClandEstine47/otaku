package com.example.feature.mediadetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.feature.R
import com.example.feature.anime.OtakuTitle

@Composable
fun MediaPerformance(
    averageScore: Int,
    meanScore: Int,
    popularity: Int,
    favourites: Int,
) {
    Column(
        modifier = Modifier.padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        OtakuTitle(id = R.string.performance)
        StatusDistributionDetailItem(
            title = stringResource(id = R.string.average),
            body = "$averageScore%",
        )
        StatusDistributionDetailItem(
            title = stringResource(id = R.string.mean),
            body = "$meanScore%",
        )
        StatusDistributionDetailItem(
            title = stringResource(id = R.string.popularity),
            body = popularity.toString(),
        )
        StatusDistributionDetailItem(
            title = stringResource(id = R.string.favourites),
            body = favourites.toString(),
        )
    }
}
