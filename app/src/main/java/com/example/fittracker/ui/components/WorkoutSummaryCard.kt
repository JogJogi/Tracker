package com.example.fittracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.util.FormatUtil

@Composable
fun WorkoutSummaryCard(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = when (activity.workoutType) {
                    "running" -> Icons.Default.DirectionsRun
                    "cycling" -> Icons.Default.DirectionsBike
                    "walking" -> Icons.Default.DirectionsWalk
                    "hiking" -> Icons.Default.Hiking
                    else -> Icons.Default.SportsGymnastics
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.workoutType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = FormatUtil.formatDuration(activity.durationMs),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = FormatUtil.formatDistanceKm(activity.distanceMeters),
                    style = MaterialTheme.typography.titleSmall
                )
                if (activity.caloriesKcal > 0) {
                    Text(
                        text = FormatUtil.formatCalories(activity.caloriesKcal),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
