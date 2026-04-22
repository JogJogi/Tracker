package com.example.fittracker.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fittracker.R
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SettingsSection(stringResource(R.string.settings_section_goals)) {
                SettingItem(
                    title = stringResource(R.string.settings_step_goal),
                    subtitle = "${uiState.stepGoal} steps"
                )
                SettingItem(
                    title = stringResource(R.string.settings_calorie_goal),
                    subtitle = "${uiState.calorieGoal} kcal"
                )
            }

            SettingsSection(stringResource(R.string.settings_section_profile)) {
                SettingItem(
                    title = stringResource(R.string.settings_height),
                    subtitle = "%.0f cm".format(uiState.heightCm)
                )
                SettingItem(
                    title = stringResource(R.string.settings_weight),
                    subtitle = "%.1f kg".format(uiState.weightKg)
                )
                SettingItem(
                    title = stringResource(R.string.settings_age),
                    subtitle = "${uiState.ageYears} years"
                )
            }

            SettingsSection(stringResource(R.string.settings_section_app)) {
                SwitchSettingItem(
                    title = stringResource(R.string.settings_use_metric),
                    checked = uiState.useMetric,
                    onCheckedChange = viewModel::setUseMetric
                )
                SwitchSettingItem(
                    title = stringResource(R.string.settings_auto_pause),
                    checked = uiState.autoPauseEnabled,
                    onCheckedChange = viewModel::setAutoPauseEnabled
                )
                SwitchSettingItem(
                    title = stringResource(R.string.settings_health_connect),
                    checked = uiState.healthConnectEnabled,
                    onCheckedChange = viewModel::setHealthConnectEnabled
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
    content()
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun SettingItem(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SwitchSettingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
