package com.example.feature.anime

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    painter: Painter,
) {
    Card(
        modifier =
            modifier
                .height(140.dp)
                .width(100.dp),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Image(
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = "poster",
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
        )
    }
}
