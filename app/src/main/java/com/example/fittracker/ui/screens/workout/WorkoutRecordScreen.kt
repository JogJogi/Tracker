package com.example.fittracker.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fittracker.R
import com.example.fittracker.service.WorkoutTrackingService
import com.example.fittracker.util.DistanceUtil
import com.example.fittracker.util.FormatUtil
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutRecordScreen(
    onBack: () -> Unit,
    viewModel: WorkoutViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.bindService(context)
        onDispose { viewModel.unbindService(context) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workout_record_title)) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Workout type selector (only when IDLE)
            if (uiState.trackingState == WorkoutTrackingService.TrackingState.IDLE) {
                WorkoutTypeSelector(
                    selected = uiState.selectedWorkoutType,
                    onSelect = viewModel::selectWorkoutType
                )
            }

            // Live stats
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MetricRow(
                        label = stringResource(R.string.workout_distance),
                        value = FormatUtil.formatDistanceKm(uiState.distanceMeters)
                    )
                    Spacer(Modifier.height(8.dp))
                    MetricRow(
                        label = stringResource(R.string.workout_duration),
                        value = FormatUtil.formatDuration(uiState.durationMs)
                    )
                    Spacer(Modifier.height(8.dp))
                    MetricRow(
                        label = stringResource(R.string.workout_pace),
                        value = FormatUtil.formatPace(
                            DistanceUtil.pace(uiState.distanceMeters, uiState.durationMs)
                        )
                    )
                    if (uiState.currentHeartRate != null) {
                        Spacer(Modifier.height(8.dp))
                        MetricRow(
                            label = stringResource(R.string.workout_heart_rate),
                            value = FormatUtil.formatHeartRate(uiState.currentHeartRate)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Control buttons
            WorkoutControls(
                state = uiState.trackingState,
                onStart = { viewModel.startWorkout(context) },
                onPause = { viewModel.pauseWorkout(context) },
                onResume = { viewModel.resumeWorkout(context) },
                onStop = { viewModel.stopWorkout(context) }
            )
        }
    }
}

@Composable
private fun WorkoutTypeSelector(selected: String, onSelect: (String) -> Unit) {
    val types = listOf("running", "cycling", "walking", "hiking")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            FilledTonalButton(
                modifier = Modifier.weight(1f),
                onClick = { onSelect(type) },
                colors = if (type == selected) {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                } else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text(type.take(4), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun WorkoutControls(
    state: WorkoutTrackingService.TrackingState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        when (state) {
            WorkoutTrackingService.TrackingState.IDLE -> {
                Button(onClick = onStart, modifier = Modifier.size(80.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.action_start))
                }
            }
            WorkoutTrackingService.TrackingState.TRACKING -> {
                FilledTonalButton(onClick = onPause, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Default.Pause, contentDescription = stringResource(R.string.action_pause))
                }
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = stringResource(R.string.action_stop))
                }
            }
            WorkoutTrackingService.TrackingState.PAUSED -> {
                Button(onClick = onResume, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.action_resume))
                }
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = stringResource(R.string.action_stop))
                }
            }
        }
    }
}
