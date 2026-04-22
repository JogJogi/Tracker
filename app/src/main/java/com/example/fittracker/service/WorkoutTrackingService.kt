package com.example.fittracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fittracker.MainActivity
import com.example.fittracker.R
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.TrackPoint
import com.example.fittracker.data.location.GpsLocationManager
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.ActivityRepository
import com.example.fittracker.util.DistanceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Foreground Service (type: location) for GPS workout tracking.
 * Bound service pattern: Activity/ViewModel can bind to get live state.
 */
class WorkoutTrackingService : Service() {

    companion object {
        const val CHANNEL_ID = "workout_tracking"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "action_start_workout"
        const val ACTION_STOP = "action_stop_workout"
        const val ACTION_PAUSE = "action_pause_workout"
        const val ACTION_RESUME = "action_resume_workout"

        const val EXTRA_WORKOUT_TYPE = "workout_type"

        fun startIntent(context: Context, workoutType: String) =
            Intent(context, WorkoutTrackingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_WORKOUT_TYPE, workoutType)
            }

        fun stopIntent(context: Context) =
            Intent(context, WorkoutTrackingService::class.java).apply {
                action = ACTION_STOP
            }
    }

    inner class TrackingBinder : Binder() {
        fun getService(): WorkoutTrackingService = this@WorkoutTrackingService
    }

    private val binder = TrackingBinder()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val activityRepository: ActivityRepository by inject()
    private val prefs: AppPreferences by inject()
    private lateinit var gpsManager: GpsLocationManager

    private var trackingJob: Job? = null
    private var currentActivityId: Long = -1L
    private var startTimeMs: Long = 0L
    private var workoutType: String = "running"

    private var lastLocation: Location? = null
    private var totalDistanceM: Float = 0f
    private var isPaused: Boolean = false

    private val _trackingState = MutableStateFlow(TrackingState.IDLE)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    private val _distanceMeters = MutableStateFlow(0f)
    val distanceMeters: StateFlow<Float> = _distanceMeters.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _currentSpeedMs = MutableStateFlow(0f)
    val currentSpeedMs: StateFlow<Float> = _currentSpeedMs.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        gpsManager = GpsLocationManager(applicationContext)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                workoutType = intent.getStringExtra(EXTRA_WORKOUT_TYPE) ?: "running"
                startTracking()
            }
            ACTION_STOP -> stopTracking()
            ACTION_PAUSE -> pauseTracking()
            ACTION_RESUME -> resumeTracking()
        }
        return START_STICKY
    }

    private fun startTracking() {
        if (_trackingState.value != TrackingState.IDLE) return

        startTimeMs = System.currentTimeMillis()
        _trackingState.value = TrackingState.TRACKING

        startForeground(NOTIFICATION_ID, buildNotification("GPS acquiring..."))

        scope.launch {
            val activityId = activityRepository.saveActivity(
                Activity(
                    workoutType = workoutType,
                    startTimeMs = startTimeMs,
                    endTimeMs = null
                )
            )
            currentActivityId = activityId

            val accuracyFilter = prefs.gpsAccuracyFilterM.first()
            trackingJob = launch {
                gpsManager.locationFlow(accuracyFilter).collect { location ->
                    if (!isPaused) handleLocation(location)
                }
            }
        }
    }

    private suspend fun handleLocation(location: Location) {
        val prev = lastLocation
        if (prev != null) {
            val distance = DistanceUtil.haversineDistanceM(
                prev.latitude, prev.longitude,
                location.latitude, location.longitude
            )
            totalDistanceM += distance
            _distanceMeters.value = totalDistanceM
        }
        lastLocation = location

        val nowMs = System.currentTimeMillis()
        _durationMs.value = nowMs - startTimeMs
        _currentSpeedMs.value = location.speed

        activityRepository.saveTrackPoint(
            TrackPoint(
                activityId = currentActivityId,
                timestampMs = location.time,
                latitude = location.latitude,
                longitude = location.longitude,
                altitudeMeters = if (location.hasAltitude()) location.altitude else null,
                accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
                speedMs = if (location.hasSpeed()) location.speed else null
            )
        )

        updateNotification("${FormatUtil.formatDistanceKm(totalDistanceM)} · ${FormatUtil.formatDuration(_durationMs.value)}")
    }

    private fun pauseTracking() {
        isPaused = true
        _trackingState.value = TrackingState.PAUSED
        updateNotification("Paused")
    }

    private fun resumeTracking() {
        isPaused = false
        lastLocation = null // Reset to avoid large distance jump on resume
        _trackingState.value = TrackingState.TRACKING
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        _trackingState.value = TrackingState.IDLE

        if (currentActivityId != -1L) {
            scope.launch {
                val endTimeMs = System.currentTimeMillis()
                val existing = activityRepository.getActivityById(currentActivityId)
                existing?.let {
                    activityRepository.updateActivity(
                        it.copy(
                            endTimeMs = endTimeMs,
                            durationMs = _durationMs.value,
                            distanceMeters = totalDistanceM
                        )
                    )
                }
                currentActivityId = -1L
                totalDistanceM = 0f
                lastLocation = null
                _distanceMeters.value = 0f
                _durationMs.value = 0L
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Workout Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows active workout progress"
            setShowBadge(false)
        }
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            stopIntent(this),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_workout_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_directions_run)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, getString(R.string.action_stop), stopIntent)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, buildNotification(text))
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    enum class TrackingState { IDLE, TRACKING, PAUSED }
}

// Lightweight format helper referenced only from this service
private object FormatUtil {
    fun formatDistanceKm(meters: Float): String =
        "%.2f km".format(meters / 1000f)

    fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }
}
