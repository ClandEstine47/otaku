package com.example.feature.anime

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OtakuTitle(
    modifier: Modifier = Modifier,
    id: Int,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    Text(
        text = stringResource(id = id),
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
    )
}

@Composable
fun OtakuTitle(
    modifier: Modifier = Modifier,
    title: String,
    color: Color,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    Text(
        text = title,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
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
        modifier = modifier,
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

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            onButtonClick()
        },
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "go back",
        )
    }
}

@Composable
fun ShareButton(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = {
            onButtonClick()
        },
    ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "share")
    }
}
