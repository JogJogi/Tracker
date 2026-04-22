package com.example.fittracker.ui.screens.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.db.entity.StepsHourly
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.StepsRepository
import com.example.fittracker.data.sensor.StepCounterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class StepsUiState(
    val todaySteps: Int = 0,
    val stepGoal: Int = 10000,
    val weeklySteps: List<StepsDaily> = emptyList(),
    val hourlySteps: List<StepsHourly> = emptyList(),
    val isSensorAvailable: Boolean = true,
    val isLoading: Boolean = true
)

class StepsViewModel(
    private val stepsRepository: StepsRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val tz = TimeZone.currentSystemDefault()
    private val today = Clock.System.now().toLocalDateTime(tz).date
    private val todayIso = today.toString()
    private val weekAgoIso = (today - DatePeriod(days = 6)).toString()

    val uiState: StateFlow<StepsUiState> = combine(
        stepsRepository.getTodayStepsFlow(todayIso),
        stepsRepository.getWeeklyStepsFlow(weekAgoIso, todayIso),
        stepsRepository.getHourlyStepsFlow(todayIso),
        prefs.stepGoal
    ) { todaySteps, weeklySteps, hourlySteps, goal ->
        StepsUiState(
            todaySteps = todaySteps?.stepCount ?: 0,
            stepGoal = goal,
            weeklySteps = weeklySteps,
            hourlySteps = hourlySteps,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StepsUiState()
    )
}
