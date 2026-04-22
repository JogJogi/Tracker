package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fittracker.data.db.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: Activity): Long

    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun delete(activity: Activity)

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getById(id: Long): Activity?

    @Query("SELECT * FROM activities ORDER BY start_time_ms DESC")
    fun getAllFlow(): Flow<List<Activity>>

    @Query("SELECT * FROM activities ORDER BY start_time_ms DESC LIMIT :limit")
    fun getRecentFlow(limit: Int): Flow<List<Activity>>

    @Query("SELECT * FROM activities WHERE workout_type = :type ORDER BY start_time_ms DESC")
    fun getByTypeFlow(type: String): Flow<List<Activity>>

    @Query("""
        SELECT * FROM activities
        WHERE start_time_ms >= :fromMs AND start_time_ms <= :toMs
        ORDER BY start_time_ms DESC
    """)
    fun getInRangeFlow(fromMs: Long, toMs: Long): Flow<List<Activity>>

    @Query("SELECT COUNT(*) FROM activities")
    fun getTotalCountFlow(): Flow<Int>

    @Query("SELECT SUM(distance_meters) FROM activities")
    fun getTotalDistanceFlow(): Flow<Float?>

    @Query("SELECT SUM(calories_kcal) FROM activities")
    fun getTotalCaloriesFlow(): Flow<Int?>

    @Query("""
        SELECT SUM(distance_meters) FROM activities
        WHERE start_time_ms >= :fromMs AND start_time_ms <= :toMs
    """)
    fun getDistanceInRangeFlow(fromMs: Long, toMs: Long): Flow<Float?>

    @Query("SELECT * FROM activities WHERE synced_to_health_connect = 0")
    suspend fun getUnsyncedActivities(): List<Activity>

    @Query("UPDATE activities SET synced_to_health_connect = 1 WHERE id = :id")
    suspend fun markSyncedToHealthConnect(id: Long)
}
