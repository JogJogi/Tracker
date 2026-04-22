package com.example.fittracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.preferences.AppPreferences
import com.example.fittracker.data.repository.ActivityRepository
import com.example.fittracker.data.repository.StepsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeUiState(
    val todaySteps: Int = 0,
    val stepGoal: Int = 10000,
    val recentActivities: List<Activity> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val stepsRepository: StepsRepository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

    val uiState: StateFlow<HomeUiState> = combine(
        stepsRepository.getTodayStepsFlow(today),
        activityRepository.getRecentActivitiesFlow(limit = 5),
        prefs.stepGoal
    ) { todaySteps, recentActivities, stepGoal ->
        HomeUiState(
            todaySteps = todaySteps?.stepCount ?: 0,
            stepGoal = stepGoal,
            recentActivities = recentActivities,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
