package com.example.fittracker.data.export

import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.BodyMeasurement
import com.example.fittracker.data.db.entity.FoodEntry
import com.example.fittracker.data.db.entity.StepsDaily
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Full JSON backup with schema version for future migration chains.
 */
class JsonExporter {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    fun exportFullBackup(
        activities: List<Activity>,
        stepsDaily: List<StepsDaily>,
        bodyMeasurements: List<BodyMeasurement>,
        foodEntries: List<FoodEntry>,
        outputStream: OutputStream
    ) {
        val backup = BackupData(
            schemaVersion = SCHEMA_VERSION,
            exportedAtMs = System.currentTimeMillis(),
            activities = activities,
            stepsDaily = stepsDaily,
            bodyMeasurements = bodyMeasurements,
            foodEntries = foodEntries
        )
        OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
            writer.write(json.encodeToString(backup))
        }
    }

    companion object {
        const val SCHEMA_VERSION = 1
    }
}

@Serializable
data class BackupData(
    val schemaVersion: Int,
    val exportedAtMs: Long,
    val activities: List<Activity>,
    val stepsDaily: List<StepsDaily>,
    val bodyMeasurements: List<BodyMeasurement>,
    val foodEntries: List<FoodEntry>
)
