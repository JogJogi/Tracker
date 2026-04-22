package com.example.fittracker.data.export

import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.StepsDaily
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Exports activities and daily steps to CSV using Apache Commons CSV (Apache 2.0).
 */
class CsvExporter {

    private val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)

    fun exportActivities(activities: List<Activity>, outputStream: OutputStream) {
        val format = CSVFormat.DEFAULT.builder()
            .setHeader(
                "id", "type", "start_time", "end_time", "duration_ms",
                "distance_m", "elevation_gain_m", "calories_kcal",
                "avg_hr_bpm", "max_hr_bpm", "avg_speed_ms", "step_count", "notes"
            )
            .build()

        OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
            CSVPrinter(writer, format).use { printer ->
                activities.forEach { a ->
                    printer.printRecord(
                        a.id,
                        a.workoutType,
                        isoFormatter.format(Instant.ofEpochMilli(a.startTimeMs)),
                        a.endTimeMs?.let { isoFormatter.format(Instant.ofEpochMilli(it)) } ?: "",
                        a.durationMs,
                        a.distanceMeters,
                        a.elevationGainMeters,
                        a.caloriesKcal,
                        a.avgHeartRateBpm ?: "",
                        a.maxHeartRateBpm ?: "",
                        a.avgSpeedMs,
                        a.stepCount ?: "",
                        a.notes
                    )
                }
            }
        }
    }

    fun exportDailySteps(steps: List<StepsDaily>, outputStream: OutputStream) {
        val format = CSVFormat.DEFAULT.builder()
            .setHeader("date", "steps", "distance_m", "calories_kcal")
            .build()

        OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
            CSVPrinter(writer, format).use { printer ->
                steps.forEach { s ->
                    printer.printRecord(s.dateIso, s.stepCount, s.distanceMeters, s.caloriesKcal)
                }
            }
        }
    }
}
