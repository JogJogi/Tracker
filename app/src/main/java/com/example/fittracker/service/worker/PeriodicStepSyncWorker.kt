package com.example.fittracker.service.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Schedules periodic step sync (every 15 minutes) so steps are always fresh
 * in Room even when the app is not in the foreground.
 */
class PeriodicStepSyncWorker(appContext: Context, params: WorkerParameters) :
    StepSyncWorker(appContext, params) {

    companion object {
        private const val WORK_NAME = "periodic_step_sync"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<PeriodicStepSyncWorker>(
                15, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
