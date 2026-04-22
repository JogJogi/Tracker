package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "body_measurements",
    indices = [Index("measured_at_ms")]
)
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "measured_at_ms")
    val measuredAtMs: Long,

    @ColumnInfo(name = "weight_kg")
    val weightKg: Float? = null,

    @ColumnInfo(name = "body_fat_percent")
    val bodyFatPercent: Float? = null,

    @ColumnInfo(name = "muscle_mass_kg")
    val muscleMassKg: Float? = null,

    @ColumnInfo(name = "bmi")
    val bmi: Float? = null,

    @ColumnInfo(name = "notes")
    val notes: String = ""
)
