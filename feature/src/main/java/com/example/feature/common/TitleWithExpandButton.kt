package com.example.feature.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TitleWithExpandButton(
    modifier: Modifier = Modifier,
    titleId: Int,
    onExpandClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OtakuTitle(
            id = titleId,
            modifier = Modifier.padding(start = 10.dp),
        )

        ExpandMediaListButton(
            modifier = Modifier,
            onButtonClick = {
                onExpandClick()
            },
        )
    }
}
