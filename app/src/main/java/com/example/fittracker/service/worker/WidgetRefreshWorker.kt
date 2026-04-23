package com.example.fittracker.service.worker

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.fittracker.ui.widget.StepsWidget
import com.example.fittracker.ui.widget.WorkoutWidget
import java.util.concurrent.TimeUnit

/**
 * Periodic WorkManager task that refreshes Glance widgets every 15 minutes.
 */
class WidgetRefreshWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(applicationContext)

        // Refresh steps widget
        manager.getGlanceIds(StepsWidget::class.java).forEach { id ->
            StepsWidget().update(applicationContext, id)
        }

        // Refresh workout widget
        manager.getGlanceIds(WorkoutWidget::class.java).forEach { id ->
            WorkoutWidget().update(applicationContext, id)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "widget_refresh"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WidgetRefreshWorker>(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
