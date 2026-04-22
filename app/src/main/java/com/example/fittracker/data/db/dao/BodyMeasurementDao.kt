package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.db.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: BodyMeasurement): Long

    @Delete
    suspend fun delete(measurement: BodyMeasurement)

    @Query("SELECT * FROM body_measurements ORDER BY measured_at_ms DESC")
    fun getAllFlow(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements ORDER BY measured_at_ms DESC LIMIT 1")
    fun getLatestFlow(): Flow<BodyMeasurement?>

    @Query("""
        SELECT * FROM body_measurements
        WHERE measured_at_ms >= :fromMs AND measured_at_ms <= :toMs
        ORDER BY measured_at_ms ASC
    """)
    fun getInRangeFlow(fromMs: Long, toMs: Long): Flow<List<BodyMeasurement>>
}
