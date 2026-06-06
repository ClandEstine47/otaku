package com.example.feature.screens.mediadetail.components

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.domain.model.common.FuzzyDate
import com.example.core.domain.model.media.Media
import com.example.core.domain.model.media.MediaListStatus
import com.example.core.domain.model.media.MediaType
import com.example.feature.R
import com.example.feature.common.OtakuTitle
import com.example.feature.screens.search.OtakuDropdownMenu
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaListEditorBottomSheet(
    media: Media,
    onSave: (
        status: MediaListStatus?,
        score: Double?,
        progress: Int?,
        repeat: Int?,
        private: Boolean?,
        hiddenFromStatusLists: Boolean?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        notes: String?,
    ) -> Unit,
    onDelete: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val mediaListEntry = media.mediaListEntry

    var selectedStatus by remember {
        mutableStateOf(mediaListEntry?.status)
    }
    var score by remember {
        mutableStateOf(mediaListEntry?.score?.takeIf { it > 0 }?.toString() ?: "")
    }
    var progress by remember {
        mutableStateOf(mediaListEntry?.progress?.takeIf { it > 0 }?.toString() ?: "")
    }
    var repeat by remember {
        mutableStateOf(mediaListEntry?.repeat?.takeIf { it > 0 }?.toString() ?: "")
    }
    var isPrivate by remember {
        mutableStateOf(mediaListEntry?.private ?: false)
    }
    var hiddenFromStatusLists by remember {
        mutableStateOf(mediaListEntry?.hiddenFromStatusLists ?: false)
    }
    var notes by remember {
        mutableStateOf(mediaListEntry?.notes ?: "")
    }
    val context = LocalContext.current
    var startedAt by remember { mutableStateOf(mediaListEntry?.startedAt) }
    var completedAt by remember { mutableStateOf(mediaListEntry?.completedAt) }
    var showOtherOptions by remember { mutableStateOf(false) }

    var isScoreFocused by remember { mutableStateOf(false) }
    var isProgressFocused by remember { mutableStateOf(false) }

    val focusedColor = MaterialTheme.colorScheme.primary
    val unFocusedColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedLabelColor = focusedColor,
            focusedBorderColor = focusedColor,
            focusedTrailingIconColor = focusedColor,
            unfocusedLabelColor = unFocusedColor,
            unfocusedBorderColor = unFocusedColor,
            unfocusedTrailingIconColor = unFocusedColor,
        )
    val textFieldShape = RoundedCornerShape(20.dp)
    val textFieldTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)

    val maxProgress = if (media.type == MediaType.ANIME) media.episodes ?: 0 else media.chapters ?: 0

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OtakuTitle(
                title = stringResource(R.string.list_editor),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        // Status Dropdown
        OtakuDropdownMenu(
            options = MediaListStatus.entries,
            currentValue = selectedStatus,
            label = stringResource(R.string.status).uppercase(),
            onValueChangedEvent = { selectedStatus = it },
            modifier = Modifier.fillMaxWidth(),
        )

        // Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = progress,
                onValueChange = { newValue ->
                    progress =
                        if (newValue.isEmpty()) {
                            ""
                        } else if (newValue.toIntOrNull() != null && (maxProgress <= 0 || newValue.toInt() <= maxProgress)) {
                            newValue
                        } else {
                            progress
                        }
                },
                label = {
                    Text(
                        text = if (media.type == MediaType.ANIME) stringResource(R.string.episodes).uppercase() else stringResource(R.string.chapters).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip,
                    )
                },
                trailingIcon = {
                    if (progress.isNotEmpty() || isProgressFocused) {
                        Text(
                            text = if (maxProgress > 0) "/$maxProgress" else "/?",
                            style = textFieldTextStyle,
                            color = unFocusedColor.copy(0.7f),
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).onFocusChanged { isProgressFocused = it.isFocused },
                shape = textFieldShape,
                colors = textFieldColors,
                textStyle = textFieldTextStyle,
                singleLine = true,
            )

            IconButton(
                onClick = {
                    val current = progress.toIntOrNull() ?: 0
                    if (current > 0) progress = (current - 1).toString()
                },
                modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
            ) {
                Icon(painterResource(R.drawable.remove), contentDescription = "Decrease")
            }

            IconButton(
                onClick = {
                    val current = progress.toIntOrNull() ?: 0
                    if (maxProgress <= 0 || current < maxProgress) progress = (current + 1).toString()
                },
                modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }

        // Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = score,
                onValueChange = { newValue ->
                    if (newValue.isEmpty()) {
                        score = ""
                    } else {
                        val doubleValue = newValue.toDoubleOrNull()
                        if (doubleValue != null && doubleValue <= 10) {
                            // Ensure only one decimal place is allowed in the text field input
                            if (!newValue.contains(".") || newValue.substringAfter(".").length <= 1) {
                                score = newValue
                            }
                        }
                    }
                },
                label = {
                    Text(
                        text = stringResource(R.string.score).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip,
                    )
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                trailingIcon = {
                    if (score.isNotEmpty() || isScoreFocused) {
                        Text(
                            text = "/10",
                            style = textFieldTextStyle,
                            color = unFocusedColor.copy(0.7f),
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .onFocusChanged { isScoreFocused = it.isFocused },
                shape = textFieldShape,
                colors = textFieldColors,
                textStyle = textFieldTextStyle,
                singleLine = true,
            )

            IconButton(
                onClick = {
                    val current = score.toDoubleOrNull() ?: 0.0
                    if (current > 0) {
                        val next = (current - 0.5).coerceAtLeast(0.0)
                        score = if (next % 1.0 == 0.0) next.toInt().toString() else String.format("%.1f", next)
                    }
                },
                modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
            ) {
                Icon(painterResource(R.drawable.remove), contentDescription = "Decrease")
            }

            IconButton(
                onClick = {
                    val current = score.toDoubleOrNull() ?: 0.0
                    if (current < 10) {
                        val next = (current + 0.5).coerceAtMost(10.0)
                        score = if (next % 1.0 == 0.0) next.toInt().toString() else String.format("%.1f", next)
                    }
                },
                modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }

        // Start and End Dates
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value =
                        startedAt?.let {
                            if (it.year != null && it.month != null && it.day != null) {
                                "${it.year}-${it.month}-${it.day}"
                            } else {
                                ""
                            }
                        } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.start_date).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Clip,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = textFieldShape,
                    colors = textFieldColors,
                    textStyle = textFieldTextStyle,
                    singleLine = true,
                )
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        startedAt = FuzzyDate(year, month + 1, dayOfMonth)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                ).apply {
                                    setButton(DialogInterface.BUTTON_NEUTRAL, "Remove") { _, _ ->
                                        startedAt = null
                                    }
                                }.show()
                            },
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value =
                        completedAt?.let {
                            if (it.year != null && it.month != null && it.day != null) {
                                "${it.year}-${it.month}-${it.day}"
                            } else {
                                ""
                            }
                        } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = stringResource(R.string.end_date).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Clip,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = textFieldShape,
                    colors = textFieldColors,
                    textStyle = textFieldTextStyle,
                    singleLine = true,
                )
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        completedAt = FuzzyDate(year, month + 1, dayOfMonth)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH),
                                ).apply {
                                    setButton(DialogInterface.BUTTON_NEUTRAL, "Remove") { _, _ ->
                                        completedAt = null
                                    }
                                }.show()
                            },
                )
            }
        }

        TextButton(
            onClick = { showOtherOptions = !showOtherOptions },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OtakuTitle(
                    title = stringResource(R.string.other),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Icon(
                    imageVector =
                        if (showOtherOptions) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        AnimatedVisibility(visible = showOtherOptions) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Repeat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = repeat,
                        onValueChange = { newValue ->
                            repeat =
                                if (newValue.isEmpty()) {
                                    ""
                                } else if (newValue.toIntOrNull() != null) {
                                    newValue
                                } else {
                                    repeat
                                }
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.repeat_count).uppercase(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                overflow = TextOverflow.Clip,
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = textFieldShape,
                        colors = textFieldColors,
                        textStyle = textFieldTextStyle,
                        singleLine = true,
                    )

                    IconButton(
                        onClick = {
                            val current = repeat.toIntOrNull() ?: 0
                            if (current > 0) repeat = (current - 1).toString()
                        },
                        modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
                    ) {
                        Icon(painterResource(R.drawable.remove), contentDescription = "Decrease")
                    }

                    IconButton(
                        onClick = {
                            val current = repeat.toIntOrNull() ?: 0
                            repeat = (current + 1).toString()
                        },
                        modifier = Modifier.border(0.5.dp, MaterialTheme.colorScheme.onBackground.copy(0.7f), CircleShape),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }

                // Toggle for Private
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OtakuTitle(
                        title = "Private",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Checkbox(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                    )
                }

                // Toggle for Hide in Home Screen
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OtakuTitle(
                        title = "Hide in Home Screen",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Checkbox(
                        checked = hiddenFromStatusLists,
                        onCheckedChange = { hiddenFromStatusLists = it },
                    )
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = {
                        Text(
                            text = stringResource(R.string.notes).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Clip,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = textFieldShape,
                    colors = textFieldColors,
                    textStyle = textFieldTextStyle,
                )
            }
        }

        // Save/Delete Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(
                onClick = {
                    onDelete()
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f),
            ) {
                OtakuTitle(
                    id = R.string.delete,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            TextButton(
                onClick = {
                    scope.launch {
                        onSave(
                            selectedStatus,
                            score.toDoubleOrNull(),
                            progress.toIntOrNull(),
                            repeat.toIntOrNull(),
                            isPrivate,
                            hiddenFromStatusLists,
                            startedAt,
                            completedAt,
                            notes,
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f),
            ) {
                OtakuTitle(
                    id = R.string.save,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
