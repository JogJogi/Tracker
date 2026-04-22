package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "heart_rate_samples",
    foreignKeys = [
        ForeignKey(
            entity = Activity::class,
            parentColumns = ["id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("timestamp_ms"), Index("activity_id")]
)
data class HeartRateSample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "timestamp_ms")
    val timestampMs: Long,

    @ColumnInfo(name = "bpm")
    val bpm: Int,

    @ColumnInfo(name = "activity_id")
    val activityId: Long? = null,

    @ColumnInfo(name = "source")
    val source: String = "ble"         // "ble", "health_connect", "sensor"
)
