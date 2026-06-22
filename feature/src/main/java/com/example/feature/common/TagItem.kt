package com.example.feature.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TagItem(
    modifier: Modifier = Modifier,
    key: Int,
    value: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        OtakuTitle(
            id = key,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.width(50.dp))
        OtakuTitle(
            title = value,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
