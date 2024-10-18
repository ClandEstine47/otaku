package com.example.feature.search

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.feature.Utils
import com.example.feature.anime.OtakuTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OtakuDropdownMenu(
    options: List<T>,
    label: String,
    onValueChangedEvent: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedValue by remember { mutableStateOf<T?>(null) }
    val focusManager = LocalFocusManager.current
    val focusedColor = MaterialTheme.colorScheme.primary
    val unFocusedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            readOnly = true,
            value = Utils.getFormattedString(selectedValue),
            onValueChange = {},
            textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Clip,
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = focusedColor,
                    focusedBorderColor = focusedColor,
                    focusedTrailingIconColor = focusedColor,
                    unfocusedLabelColor = unFocusedColor,
                    unfocusedBorderColor = unFocusedColor,
                    unfocusedTrailingIconColor = unFocusedColor,
                ),
            modifier =
                Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                focusManager.clearFocus()
            },
        ) {
            options.forEach { option: T ->
                DropdownMenuItem(
                    text = { OtakuTitle(title = Utils.getFormattedString(option), style = MaterialTheme.typography.titleSmall) },
                    onClick = {
                        expanded = false
                        selectedValue = option
                        onValueChangedEvent(option)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtakuDropdownMenuPreview() {
    OtakuDropdownMenu(
        label = "Season",
        options = listOf("WINTER", "SUMMER", "SPRING", "FALL"),
        onValueChangedEvent = {},
    )
}
