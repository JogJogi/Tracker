package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "workout_type")
    val workoutType: String,           // "running", "cycling", "walking", "hiking", "other"

    @ColumnInfo(name = "start_time_ms")
    val startTimeMs: Long,             // epoch millis

    @ColumnInfo(name = "end_time_ms")
    val endTimeMs: Long?,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long = 0,          // active duration (excl. auto-pause)

    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Float = 0f,

    @ColumnInfo(name = "elevation_gain_meters")
    val elevationGainMeters: Float = 0f,

    @ColumnInfo(name = "calories_kcal")
    val caloriesKcal: Int = 0,

    @ColumnInfo(name = "avg_heart_rate_bpm")
    val avgHeartRateBpm: Int? = null,

    @ColumnInfo(name = "max_heart_rate_bpm")
    val maxHeartRateBpm: Int? = null,

    @ColumnInfo(name = "avg_speed_ms")
    val avgSpeedMs: Float = 0f,        // m/s

    @ColumnInfo(name = "max_speed_ms")
    val maxSpeedMs: Float = 0f,

    @ColumnInfo(name = "step_count")
    val stepCount: Int? = null,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "gpx_file_path")
    val gpxFilePath: String? = null,   // path inside app-private storage

    @ColumnInfo(name = "synced_to_health_connect")
    val syncedToHealthConnect: Boolean = false
)
