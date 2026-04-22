package com.example.fittracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittracker.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val stepGoal: Int = 10000,
    val calorieGoal: Int = 2000,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val ageYears: Int = 30,
    val useMetric: Boolean = true,
    val healthConnectEnabled: Boolean = false,
    val autoPauseEnabled: Boolean = true
)

class SettingsViewModel(private val prefs: AppPreferences) : ViewModel() {

    // Combine in two groups of 4 (max supported by combine overloads), then merge
    private val group1 = combine(prefs.stepGoal, prefs.calorieGoal, prefs.heightCm, prefs.weightKg) {
        stepGoal, calorieGoal, heightCm, weightKg ->
        Quadruple(stepGoal, calorieGoal, heightCm, weightKg)
    }
    private val group2 = combine(prefs.ageYears, prefs.useMetric, prefs.healthConnectEnabled, prefs.autoPauseEnabled) {
        ageYears, useMetric, hcEnabled, autoPause ->
        Quadruple(ageYears, useMetric, hcEnabled, autoPause)
    }

    val uiState: StateFlow<SettingsUiState> = combine(group1, group2) { g1, g2 ->
        SettingsUiState(
            stepGoal = g1.a,
            calorieGoal = g1.b,
            heightCm = g1.c,
            weightKg = g1.d,
            ageYears = g2.a,
            useMetric = g2.b,
            healthConnectEnabled = g2.c,
            autoPauseEnabled = g2.d
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun setStepGoal(goal: Int) = viewModelScope.launch { prefs.setStepGoal(goal) }
    fun setCalorieGoal(goal: Int) = viewModelScope.launch { prefs.setCalorieGoal(goal) }
    fun setHeightCm(cm: Float) = viewModelScope.launch { prefs.setHeightCm(cm) }
    fun setWeightKg(kg: Float) = viewModelScope.launch { prefs.setWeightKg(kg) }
    fun setAgeYears(age: Int) = viewModelScope.launch { prefs.setAgeYears(age) }
    fun setUseMetric(metric: Boolean) = viewModelScope.launch { prefs.setUseMetric(metric) }
    fun setHealthConnectEnabled(enabled: Boolean) = viewModelScope.launch { prefs.setHealthConnectEnabled(enabled) }
    fun setAutoPauseEnabled(enabled: Boolean) = viewModelScope.launch { prefs.setAutoPauseEnabled(enabled) }

    private data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}
