package com.example.fittracker.ui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fittracker.R
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.repository.ActivityRepository
import com.example.fittracker.util.DistanceUtil
import com.example.fittracker.util.FormatUtil
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    activityId: Long,
    onBack: () -> Unit,
    activityRepository: ActivityRepository = koinInject()
) {
    var activity by remember { mutableStateOf<Activity?>(null) }

    LaunchedEffect(activityId) {
        activity = activityRepository.getActivityById(activityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(activity?.workoutType?.replaceFirstChar { it.uppercase() } ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: export GPX */ }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(R.string.action_export))
                    }
                }
            )
        }
    ) { padding ->
        val act = activity ?: return@Scaffold

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatRow(stringResource(R.string.workout_distance), FormatUtil.formatDistanceKm(act.distanceMeters))
                        StatRow(stringResource(R.string.workout_duration), FormatUtil.formatDuration(act.durationMs))
                        StatRow(
                            stringResource(R.string.workout_pace),
                            FormatUtil.formatPace(DistanceUtil.pace(act.distanceMeters, act.durationMs))
                        )
                        if (act.caloriesKcal > 0)
                            StatRow(stringResource(R.string.workout_calories), FormatUtil.formatCalories(act.caloriesKcal))
                        act.avgHeartRateBpm?.let { hr ->
                            StatRow(stringResource(R.string.workout_avg_hr), FormatUtil.formatHeartRate(hr))
                        }
                        act.maxHeartRateBpm?.let { hr ->
                            StatRow(stringResource(R.string.workout_max_hr), FormatUtil.formatHeartRate(hr))
                        }
                        if (act.elevationGainMeters > 0)
                            StatRow(stringResource(R.string.workout_elevation), "%.0f m".format(act.elevationGainMeters))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
