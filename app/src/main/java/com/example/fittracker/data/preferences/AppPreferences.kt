package com.example.fittracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val STEP_GOAL = intPreferencesKey("step_goal")
        val STEP_SENSOR_OFFSET = intPreferencesKey("step_sensor_offset")
        val STEP_SENSOR_OFFSET_DATE = stringPreferencesKey("step_sensor_offset_date")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val AGE_YEARS = intPreferencesKey("age_years")
        val USE_METRIC = booleanPreferencesKey("use_metric")
        val CALORIE_GOAL = intPreferencesKey("calorie_goal")
        val HEALTH_CONNECT_ENABLED = booleanPreferencesKey("health_connect_enabled")
        val LAST_BLE_DEVICE_ADDRESS = stringPreferencesKey("last_ble_device_address")
        val AUTO_PAUSE_ENABLED = booleanPreferencesKey("auto_pause_enabled")
        val GPS_ACCURACY_FILTER_M = floatPreferencesKey("gps_accuracy_filter_m")
        val WIDGET_LAST_UPDATED_MS = longPreferencesKey("widget_last_updated_ms")
    }

    val stepGoal: Flow<Int> = context.dataStore.data.map { it[Keys.STEP_GOAL] ?: 10000 }
    val stepSensorOffset: Flow<Int> = context.dataStore.data.map { it[Keys.STEP_SENSOR_OFFSET] ?: 0 }
    val stepSensorOffsetDate: Flow<String> = context.dataStore.data.map { it[Keys.STEP_SENSOR_OFFSET_DATE] ?: "" }
    val heightCm: Flow<Float> = context.dataStore.data.map { it[Keys.HEIGHT_CM] ?: 170f }
    val weightKg: Flow<Float> = context.dataStore.data.map { it[Keys.WEIGHT_KG] ?: 70f }
    val ageYears: Flow<Int> = context.dataStore.data.map { it[Keys.AGE_YEARS] ?: 30 }
    val useMetric: Flow<Boolean> = context.dataStore.data.map { it[Keys.USE_METRIC] ?: true }
    val calorieGoal: Flow<Int> = context.dataStore.data.map { it[Keys.CALORIE_GOAL] ?: 2000 }
    val healthConnectEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.HEALTH_CONNECT_ENABLED] ?: false }
    val lastBleDeviceAddress: Flow<String?> = context.dataStore.data.map { it[Keys.LAST_BLE_DEVICE_ADDRESS] }
    val autoPauseEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_PAUSE_ENABLED] ?: true }
    val gpsAccuracyFilterM: Flow<Float> = context.dataStore.data.map { it[Keys.GPS_ACCURACY_FILTER_M] ?: 50f }

    suspend fun setStepGoal(goal: Int) = context.dataStore.edit { it[Keys.STEP_GOAL] = goal }
    suspend fun setStepSensorOffset(offset: Int) = context.dataStore.edit { it[Keys.STEP_SENSOR_OFFSET] = offset }
    suspend fun setStepSensorOffsetDate(date: String) = context.dataStore.edit { it[Keys.STEP_SENSOR_OFFSET_DATE] = date }
    suspend fun setHeightCm(cm: Float) = context.dataStore.edit { it[Keys.HEIGHT_CM] = cm }
    suspend fun setWeightKg(kg: Float) = context.dataStore.edit { it[Keys.WEIGHT_KG] = kg }
    suspend fun setAgeYears(age: Int) = context.dataStore.edit { it[Keys.AGE_YEARS] = age }
    suspend fun setUseMetric(metric: Boolean) = context.dataStore.edit { it[Keys.USE_METRIC] = metric }
    suspend fun setCalorieGoal(goal: Int) = context.dataStore.edit { it[Keys.CALORIE_GOAL] = goal }
    suspend fun setHealthConnectEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.HEALTH_CONNECT_ENABLED] = enabled }
    suspend fun setLastBleDeviceAddress(address: String?) = context.dataStore.edit {
        if (address != null) it[Keys.LAST_BLE_DEVICE_ADDRESS] = address
        else it.remove(Keys.LAST_BLE_DEVICE_ADDRESS)
    }
    suspend fun setAutoPauseEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.AUTO_PAUSE_ENABLED] = enabled }
    suspend fun setGpsAccuracyFilterM(meters: Float) = context.dataStore.edit { it[Keys.GPS_ACCURACY_FILTER_M] = meters }
    suspend fun setWidgetLastUpdatedMs(ms: Long) = context.dataStore.edit { it[Keys.WIDGET_LAST_UPDATED_MS] = ms }
}
