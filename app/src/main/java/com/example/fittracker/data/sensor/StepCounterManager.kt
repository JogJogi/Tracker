package com.example.fittracker.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Wraps TYPE_STEP_COUNTER (hardware-accelerated, battery-efficient).
 * The sensor reports cumulative steps since last reboot, so callers must
 * manage the offset (saved in AppPreferences, reset on BOOT_COMPLETED).
 */
class StepCounterManager(context: Context) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    val isAvailable: Boolean
        get() = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null

    /**
     * Emits the raw cumulative step value from the hardware sensor.
     * Caller subtracts the stored boot-offset to get steps-today.
     *
     * Uses maxReportLatencyUs = 60s so the sensor hub batches, minimizing wakeups.
     */
    fun rawStepCounterFlow(): Flow<Int> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            ?: run { close(); return@callbackFlow }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(event.values[0].toInt())
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        val registered = sensorManager.registerListener(
            listener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL,
            60_000_000 // 60 s report latency — batching mode
        )

        if (!registered) close(IllegalStateException("Failed to register step counter sensor"))

        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
