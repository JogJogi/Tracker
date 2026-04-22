package com.example.fittracker.service.worker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.StepsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.resume

/**
 * Periodic WorkManager task that reads the step counter hardware sensor once
 * and persists today's step count to Room. No foreground service needed for
 * daily aggregation — the hardware sensor hub batches data.
 */
class StepSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val stepsRepository: StepsRepository by inject()
    private val prefs: AppPreferences by inject()

    override suspend fun doWork(): Result {
        val sensorManager = applicationContext
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: return Result.success() // Device has no step counter — skip silently

        val rawCount = readSensorOnce(sensorManager, sensor) ?: return Result.retry()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        val offset = prefs.stepSensorOffset.first()
        val offsetDate = prefs.stepSensorOffsetDate.first()

        val adjustedOffset = if (offsetDate == today) offset else {
            // First read of the day or after reboot: establish new offset
            prefs.setStepSensorOffset(rawCount)
            prefs.setStepSensorOffsetDate(today)
            rawCount
        }

        val stepsToday = maxOf(0, rawCount - adjustedOffset)
        val nowMs = System.currentTimeMillis()

        stepsRepository.upsertDailySteps(
            StepsDaily(
                dateIso = today,
                stepCount = stepsToday,
                updatedAtMs = nowMs
            )
        )

        return Result.success()
    }

    private suspend fun readSensorOnce(
        sensorManager: SensorManager,
        sensor: Sensor
    ): Int? = suspendCancellableCoroutine { cont ->
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                sensorManager.unregisterListener(this)
                if (cont.isActive) cont.resume(event.values[0].toInt())
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) = Unit
        }
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        cont.invokeOnCancellation { sensorManager.unregisterListener(listener) }
    }
}
