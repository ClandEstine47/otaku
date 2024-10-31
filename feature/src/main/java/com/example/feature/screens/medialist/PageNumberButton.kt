package com.example.feature.screens.medialist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.feature.common.OtakuTitle

@Composable
fun PageNumberButton(
    modifier: Modifier = Modifier,
    pageNumber: Int,
) {
    Button(
        onClick = {},
        modifier = modifier.clip(RoundedCornerShape(0.dp)),
        enabled = true,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = ButtonDefaults.elevatedButtonElevation(),
    ) {
        OtakuTitle(
            title = pageNumber.toString(),
            color = MaterialTheme.colorScheme.background,
        )
    }
}

@Composable
fun PageSelectorButton(
    modifier: Modifier = Modifier,
    buttonIcon: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val disabledColor = ButtonDefaults.buttonColors().disabledContainerColor
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier.clip(RoundedCornerShape(0.dp)),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, disabledContainerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, if (enabled) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) else disabledColor),
        elevation = ButtonDefaults.elevatedButtonElevation(),
    ) {
        OtakuTitle(
            title = buttonIcon,
            color = if (enabled) MaterialTheme.colorScheme.onBackground else disabledColor,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Preview
@Composable
fun PageNumberButtonPreview(modifier: Modifier = Modifier) {
    PageNumberButton(pageNumber = 10)
}

@Preview
@Composable
fun PageSelectorButtonPreview(modifier: Modifier = Modifier) {
    PageSelectorButton(
        buttonIcon = "<",
        enabled = true,
        onClick = {},
    )
}
