package com.example.feature.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.settings.AppColor
import com.example.core.domain.model.settings.AppTheme
import com.example.core.navigation.NavActionManager
import com.example.feature.R
import com.example.feature.common.BackButton
import com.example.feature.common.OtakuTitle
import com.example.feature.screens.search.OtakuDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeView(
    navActionManager: NavActionManager,
    viewModel: ThemeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OtakuTitle(
                        title = stringResource(R.string.theme),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                navigationIcon = {
                    BackButton {
                        navActionManager.navigateBack()
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            // Theme Mode Selection
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                OtakuTitle(
                    title = stringResource(R.string.theme_mode),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ModeCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.light_mode,
                        label = stringResource(R.string.light),
                        isSelected = uiState.selectedTheme == AppTheme.LIGHT,
                        onClick = { viewModel.onThemeSelected(AppTheme.LIGHT) },
                    )
                    ModeCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.dark_mode,
                        label = stringResource(R.string.dark),
                        isSelected = uiState.selectedTheme == AppTheme.DARK,
                        onClick = { viewModel.onThemeSelected(AppTheme.DARK) },
                    )
                    ModeCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.auto_mode,
                        label = stringResource(R.string.system),
                        isSelected = uiState.selectedTheme == AppTheme.SYSTEM,
                        onClick = { viewModel.onThemeSelected(AppTheme.SYSTEM) },
                    )
                }
            }

            // Theme Color Selection
            Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                OtakuTitle(
                    title = stringResource(R.string.theme_color),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                OtakuDropdownMenu(
                    options = AppColor.entries.toList(),
                    currentValue = uiState.selectedColor,
                    onValueChangedEvent = { viewModel.onColorSelected(it) },
                    selectedTextUpperCase = true,
                )
            }

            // OLED Toggle
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = uiState.selectedTheme != AppTheme.LIGHT) {
                            viewModel.onOledChanged(!uiState.oledEnabled)
                        }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.oled_mode),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (uiState.selectedTheme != AppTheme.LIGHT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    OtakuTitle(
                        title = stringResource(R.string.oled_mode),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (uiState.selectedTheme != AppTheme.LIGHT) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                        fontWeight = FontWeight.SemiBold,
                    )

                    OtakuTitle(
                        title = stringResource(R.string.oled_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.selectedTheme != AppTheme.LIGHT) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                        fontWeight = FontWeight.Normal,
                    )
                }

                Switch(
                    checked = (uiState.oledEnabled && uiState.selectedTheme != AppTheme.LIGHT),
                    onCheckedChange = { viewModel.onOledChanged(it) },
                    enabled = uiState.selectedTheme != AppTheme.LIGHT,
                )
            }
        }
    }
}

@Composable
fun ModeCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    Card(
        modifier =
            modifier
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OtakuTitle(
                title = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
        }
    }
}
