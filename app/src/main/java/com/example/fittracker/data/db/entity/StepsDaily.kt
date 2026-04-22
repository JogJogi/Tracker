package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "steps_daily",
    primaryKeys = ["date_iso"],
    indices = [Index("date_iso")]
)
data class StepsDaily(
    @ColumnInfo(name = "date_iso")
    val dateIso: String,               // "2025-03-15"

    @ColumnInfo(name = "step_count")
    val stepCount: Int,

    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Float = 0f,

    @ColumnInfo(name = "calories_kcal")
    val caloriesKcal: Int = 0,

    @ColumnInfo(name = "updated_at_ms")
    val updatedAtMs: Long
)
