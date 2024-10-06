package com.example.feature.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.domain.model.media.MediaType
import com.example.feature.R
import com.example.feature.anime.OtakuTitle

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    mediaType: MediaType,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
                .height(60.dp)
                .padding(horizontal = 50.dp)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50.dp),
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OtakuTitle(id = if (mediaType == MediaType.ANIME) R.string.anime else R.string.manga)
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        mediaType = MediaType.ANIME,
    )
}
