package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "steps_hourly",
    primaryKeys = ["date_iso", "hour"],
    indices = [Index("date_iso")]
)
data class StepsHourly(
    @ColumnInfo(name = "date_iso")
    val dateIso: String,               // "2025-03-15"

    @ColumnInfo(name = "hour")
    val hour: Int,                     // 0-23

    @ColumnInfo(name = "step_count")
    val stepCount: Int,

    @ColumnInfo(name = "updated_at_ms")
    val updatedAtMs: Long
)
