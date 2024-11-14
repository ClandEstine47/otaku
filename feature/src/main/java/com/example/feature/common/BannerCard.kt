package com.example.feature.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.R

@Composable
fun BannerCard(
    bannerPainter: Painter,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(464.dp)
                .absolutePadding(),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RectangleShape,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                painter = bannerPainter,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .blur(
                            radiusX = 8.dp,
                            radiusY = 8.dp,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded,
                        ),
                contentScale = ContentScale.Crop,
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        MaterialTheme.colorScheme.background,
                                        Color.Transparent,
                                    ),
                                startY = Float.POSITIVE_INFINITY,
                                endY = 0f,
                            ),
                        ),
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
fun BannerImagePreview() {
    BannerCard(
        bannerPainter = painterResource(id = R.drawable.anime_banner_preview),
    )
}
