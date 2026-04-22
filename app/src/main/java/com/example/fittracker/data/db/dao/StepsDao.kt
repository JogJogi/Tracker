package com.example.fittracker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.db.entity.StepsHourly
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {

    // Daily
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDaily(stepsDaily: StepsDaily)

    @Query("SELECT * FROM steps_daily WHERE date_iso = :dateIso")
    suspend fun getDailyForDate(dateIso: String): StepsDaily?

    @Query("SELECT * FROM steps_daily WHERE date_iso = :dateIso")
    fun getDailyForDateFlow(dateIso: String): Flow<StepsDaily?>

    @Query("SELECT * FROM steps_daily WHERE date_iso >= :fromDate ORDER BY date_iso ASC")
    fun getDailyFromFlow(fromDate: String): Flow<List<StepsDaily>>

    @Query("SELECT * FROM steps_daily WHERE date_iso >= :fromDate AND date_iso <= :toDate ORDER BY date_iso ASC")
    fun getDailyInRangeFlow(fromDate: String, toDate: String): Flow<List<StepsDaily>>

    @Query("SELECT SUM(step_count) FROM steps_daily WHERE date_iso >= :fromDate AND date_iso <= :toDate")
    fun getSumInRange(fromDate: String, toDate: String): Flow<Int?>

    // Hourly
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHourly(stepsHourly: StepsHourly)

    @Query("SELECT * FROM steps_hourly WHERE date_iso = :dateIso ORDER BY hour ASC")
    fun getHourlyForDateFlow(dateIso: String): Flow<List<StepsHourly>>
}
