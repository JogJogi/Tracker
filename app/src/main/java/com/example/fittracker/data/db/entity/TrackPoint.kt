package com.example.fittracker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_points",
    foreignKeys = [
        ForeignKey(
            entity = Activity::class,
            parentColumns = ["id"],
            childColumns = ["activity_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("activity_id"), Index("timestamp_ms")]
)
data class TrackPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "activity_id")
    val activityId: Long,

    @ColumnInfo(name = "timestamp_ms")
    val timestampMs: Long,             // epoch millis

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "altitude_meters")
    val altitudeMeters: Double? = null,

    @ColumnInfo(name = "accuracy_meters")
    val accuracyMeters: Float? = null,

    @ColumnInfo(name = "speed_ms")
    val speedMs: Float? = null,        // m/s

    @ColumnInfo(name = "heart_rate_bpm")
    val heartRateBpm: Int? = null,

    @ColumnInfo(name = "cadence_rpm")
    val cadenceRpm: Int? = null
)
