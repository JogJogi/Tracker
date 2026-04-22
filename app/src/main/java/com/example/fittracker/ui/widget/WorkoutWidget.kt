package com.example.fittracker.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.fittracker.MainActivity
import com.example.fittracker.data.repository.ActivityRepository
import com.example.fittracker.util.FormatUtil
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 4×2 home screen widget showing the last completed workout summary.
 */
class WorkoutWidget : GlanceAppWidget(), KoinComponent {

    private val activityRepository: ActivityRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val lastWorkout = activityRepository.getRecentActivitiesFlow(limit = 1).first().firstOrNull()

        provideContent {
            WorkoutWidgetContent(
                workoutType = lastWorkout?.workoutType ?: "",
                distanceM = lastWorkout?.distanceMeters ?: 0f,
                durationMs = lastWorkout?.durationMs ?: 0L,
                hasWorkout = lastWorkout != null
            )
        }
    }
}

@Composable
private fun WorkoutWidgetContent(
    workoutType: String,
    distanceM: Float,
    durationMs: Long,
    hasWorkout: Boolean
) {
    GlanceTheme {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
                .padding(12.dp)
        ) {
            Text(
                text = if (hasWorkout) "Last ${workoutType.replaceFirstChar { it.uppercase() }}" else "No workouts yet",
                style = TextStyle(fontSize = 13.sp, color = GlanceTheme.colors.onSurfaceVariant)
            )
            if (hasWorkout) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = FormatUtil.formatDistanceKm(distanceM),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlanceTheme.colors.primary
                            )
                        )
                        Text(
                            text = "distance",
                            style = TextStyle(fontSize = 11.sp, color = GlanceTheme.colors.onSurfaceVariant)
                        )
                    }
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = FormatUtil.formatDuration(durationMs),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlanceTheme.colors.primary
                            )
                        )
                        Text(
                            text = "duration",
                            style = TextStyle(fontSize = 11.sp, color = GlanceTheme.colors.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

class WorkoutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorkoutWidget()
}
