package com.example.feature.anime

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.R

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    score: Double,
    displayScore: Boolean = true,
) {
    Card(
        modifier =
            modifier
                .height(140.dp)
                .width(100.dp),
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = "poster",
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
            )

            if (displayScore) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(bottomStart = 20.dp))
                            .padding(start = 10.dp, top = 2.dp, bottom = 2.dp, end = 2.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background,
                        )
                        Icon(
                            imageVector = Icons.Default.Star,
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.background,
                            contentDescription = "score",
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageCardPreview() {
    ImageCard(
        painter = painterResource(id = R.drawable.anime_cover_preview),
        score = 8.9,
    )
}
