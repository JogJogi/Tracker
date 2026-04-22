package com.example.fittracker.ui.screens.workout

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.service.WorkoutTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WorkoutRecordUiState(
    val trackingState: WorkoutTrackingService.TrackingState = WorkoutTrackingService.TrackingState.IDLE,
    val distanceMeters: Float = 0f,
    val durationMs: Long = 0L,
    val currentSpeedMs: Float = 0f,
    val currentHeartRate: Int? = null,
    val selectedWorkoutType: String = "running",
    val isServiceBound: Boolean = false
)

class WorkoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutRecordUiState())
    val uiState: StateFlow<WorkoutRecordUiState> = _uiState.asStateFlow()

    private var trackingService: WorkoutTrackingService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            val service = (binder as WorkoutTrackingService.TrackingBinder).getService()
            trackingService = service
            _uiState.update { it.copy(isServiceBound = true) }
            observeService(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            trackingService = null
            _uiState.update { it.copy(isServiceBound = false) }
        }
    }

    fun bindService(context: Context) {
        Intent(context, WorkoutTrackingService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        context.unbindService(serviceConnection)
    }

    private fun observeService(service: WorkoutTrackingService) {
        viewModelScope.run {
            kotlinx.coroutines.launch {
                service.trackingState.collect { state ->
                    _uiState.update { it.copy(trackingState = state) }
                }
            }
            kotlinx.coroutines.launch {
                service.distanceMeters.collect { dist ->
                    _uiState.update { it.copy(distanceMeters = dist) }
                }
            }
            kotlinx.coroutines.launch {
                service.durationMs.collect { dur ->
                    _uiState.update { it.copy(durationMs = dur) }
                }
            }
            kotlinx.coroutines.launch {
                service.currentSpeedMs.collect { speed ->
                    _uiState.update { it.copy(currentSpeedMs = speed) }
                }
            }
        }
    }

    fun startWorkout(context: Context) {
        context.startForegroundService(
            WorkoutTrackingService.startIntent(context, _uiState.value.selectedWorkoutType)
        )
    }

    fun stopWorkout(context: Context) {
        context.startService(WorkoutTrackingService.stopIntent(context))
    }

    fun pauseWorkout(context: Context) {
        context.startService(
            Intent(context, WorkoutTrackingService::class.java).apply {
                action = WorkoutTrackingService.ACTION_PAUSE
            }
        )
    }

    fun resumeWorkout(context: Context) {
        context.startService(
            Intent(context, WorkoutTrackingService::class.java).apply {
                action = WorkoutTrackingService.ACTION_RESUME
            }
        )
    }

    fun selectWorkoutType(type: String) {
        _uiState.update { it.copy(selectedWorkoutType = type) }
    }
}
