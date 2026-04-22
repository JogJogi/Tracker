package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.db.entity.HeartRateSample
import kotlinx.coroutines.flow.Flow

@Dao
interface HeartRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sample: HeartRateSample): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(samples: List<HeartRateSample>)

    @Query("SELECT * FROM heart_rate_samples WHERE activity_id = :activityId ORDER BY timestamp_ms ASC")
    fun getForActivityFlow(activityId: Long): Flow<List<HeartRateSample>>

    @Query("SELECT * FROM heart_rate_samples WHERE activity_id = :activityId ORDER BY timestamp_ms ASC")
    suspend fun getForActivity(activityId: Long): List<HeartRateSample>

    @Query("""
        SELECT * FROM heart_rate_samples
        WHERE timestamp_ms >= :fromMs AND timestamp_ms <= :toMs
        ORDER BY timestamp_ms ASC
    """)
    fun getInRangeFlow(fromMs: Long, toMs: Long): Flow<List<HeartRateSample>>

    @Query("SELECT AVG(bpm) FROM heart_rate_samples WHERE activity_id = :activityId")
    suspend fun getAvgBpmForActivity(activityId: Long): Float?

    @Query("SELECT MAX(bpm) FROM heart_rate_samples WHERE activity_id = :activityId")
    suspend fun getMaxBpmForActivity(activityId: Long): Int?
}
