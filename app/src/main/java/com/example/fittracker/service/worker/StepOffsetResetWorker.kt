package com.example.fittracker.service.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fittracker.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Resets the step sensor offset to the current raw sensor value on reboot.
 * This ensures daily step counts restart from 0 after a device restart.
 */
class StepOffsetResetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val prefs: AppPreferences by inject()

    override suspend fun doWork(): Result {
        // Reset offset by setting it to 0; the next reading will establish a new baseline.
        // The actual sensor value will be captured by StepsViewModel on next observation.
        prefs.setStepSensorOffset(0)
        prefs.setStepSensorOffsetDate("")
        return Result.success()
    }
}
