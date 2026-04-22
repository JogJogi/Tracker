package com.example.fittracker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.example.fittracker.data.db.dao.ActivityDao
import com.example.fittracker.data.db.dao.BodyMeasurementDao
import com.example.fittracker.data.db.dao.FoodDao
import com.example.fittracker.data.db.dao.HeartRateDao
import com.example.fittracker.data.db.dao.StepsDao
import com.example.fittracker.data.db.dao.TrackPointDao
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.BodyMeasurement
import com.example.fittracker.data.db.entity.Food
import com.example.fittracker.data.db.entity.FoodEntry
import com.example.fittracker.data.db.entity.HeartRateSample
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.db.entity.StepsHourly
import com.example.fittracker.data.db.entity.TrackPoint

@Database(
    entities = [
        Activity::class,
        TrackPoint::class,
        StepsDaily::class,
        StepsHourly::class,
        HeartRateSample::class,
        Food::class,
        FoodEntry::class,
        BodyMeasurement::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun trackPointDao(): TrackPointDao
    abstract fun stepsDao(): StepsDao
    abstract fun heartRateDao(): HeartRateDao
    abstract fun foodDao(): FoodDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao

    companion object {
        const val DATABASE_NAME = "fittracker.db"

        // Add migrations here as the schema evolves.
        // Example: val MIGRATION_1_2 = object : Migration(1, 2) { ... }
        val MIGRATIONS: Array<Migration> = emptyArray()
    }
}
