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
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.fittracker.MainActivity
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.StepsRepository
import com.example.fittracker.util.FormatUtil
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * 2×2 home screen widget showing today's steps and progress toward goal.
 * Updated by WorkManager every 15 min and on demand after step sync.
 */
class StepsWidget : GlanceAppWidget(), KoinComponent {

    private val stepsRepository: StepsRepository by inject()
    private val prefs: AppPreferences by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val today = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val todaySteps = stepsRepository.getTodayStepsFlow(today).first()?.stepCount ?: 0
        val goal = prefs.stepGoal.first()

        provideContent {
            StepsWidgetContent(steps = todaySteps, goal = goal)
        }
    }
}

@Composable
private fun StepsWidgetContent(steps: Int, goal: Int) {
    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .clickable(actionStartActivity<MainActivity>())
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = FormatUtil.formatSteps(steps),
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )
                )
                Text(
                    text = "steps",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSurface
                    )
                )
                Text(
                    text = "/ ${FormatUtil.formatSteps(goal)}",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}

class StepsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StepsWidget()
}
