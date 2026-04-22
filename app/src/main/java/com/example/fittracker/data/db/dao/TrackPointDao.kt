package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.db.entity.TrackPoint
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trackPoint: TrackPoint): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trackPoints: List<TrackPoint>)

    @Query("SELECT * FROM track_points WHERE activity_id = :activityId ORDER BY timestamp_ms ASC")
    fun getForActivityFlow(activityId: Long): Flow<List<TrackPoint>>

    @Query("SELECT * FROM track_points WHERE activity_id = :activityId ORDER BY timestamp_ms ASC")
    suspend fun getForActivity(activityId: Long): List<TrackPoint>

    @Query("SELECT COUNT(*) FROM track_points WHERE activity_id = :activityId")
    suspend fun countForActivity(activityId: Long): Int

    @Query("DELETE FROM track_points WHERE activity_id = :activityId")
    suspend fun deleteForActivity(activityId: Long)
}
