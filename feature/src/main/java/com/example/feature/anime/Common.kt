package com.example.feature.anime

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OtakuTitle(
    modifier: Modifier = Modifier,
    id: Int,
) {
    Text(
        text = stringResource(id = id),
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun OtakuTitle(
    modifier: Modifier = Modifier,
    title: String,
    color: Color,
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}

@Composable
fun OtakuImageCardTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Normal,
        maxLines = 2,
        minLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.width(100.dp),
    )
}

@Composable
fun ExpandMediaListButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    IconButton(
        onClick = {
            onButtonClick()
        },
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowForward,
            contentDescription = "expand media list",
        )
    }
}
