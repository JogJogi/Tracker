package com.example.fittracker.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.repository.ActivityRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WorkoutListUiState(
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = true
)

class WorkoutListViewModel(private val activityRepository: ActivityRepository) : ViewModel() {

    val uiState: StateFlow<WorkoutListUiState> = activityRepository.getAllActivitiesFlow()
        .map { activities -> WorkoutListUiState(activities = activities, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WorkoutListUiState()
        )

    fun deleteActivity(activity: Activity) {
        viewModelScope.launch { activityRepository.deleteActivity(activity) }
    }
}
