package com.example.feature.screens.mediadetail.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.rememberAsyncImagePainter
import com.example.core.domain.model.media.MediaExternalLink
import com.example.feature.R
import com.example.feature.common.OtakuTitle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaExternalLinks(
    externalLinks: List<MediaExternalLink>,
) {
    OtakuTitle(id = R.string.links)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        for (externalLink in externalLinks) {
            ExternalLinkItem(
                siteName = externalLink.site ?: "",
                color = externalLink.color ?: "#1b263b",
                icon = externalLink.icon ?: "",
                url = externalLink.url ?: "",
            )
        }
    }
}

@Composable
fun ExternalLinkItem(
    siteName: String,
    color: String,
    icon: String,
    url: String,
) {
    val context = LocalContext.current
    val siteIcon =
        rememberAsyncImagePainter(
            model = icon,
        )
    Box(
        modifier =
            Modifier
                .padding(3.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(color.toColorInt()))
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
    ) {
        Row(
            modifier =
                Modifier
                    .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon.isNotBlank()) {
                Image(
                    painter = siteIcon,
                    contentScale = ContentScale.Crop,
                    contentDescription = "site icon",
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Image(
                    painter = painterResource(id = if (siteName == "Official Site") R.drawable.anilist else R.drawable.link),
                    contentDescription = "site icon",
                    modifier = Modifier.size(20.dp),
                )
            }

            OtakuTitle(title = siteName, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
