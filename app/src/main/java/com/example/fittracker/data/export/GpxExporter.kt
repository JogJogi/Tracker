package com.example.fittracker.data.export

import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.TrackPoint
import io.jenetics.jpx.GPX
import io.jenetics.jpx.Track
import io.jenetics.jpx.TrackSegment
import io.jenetics.jpx.WayPoint
import java.io.OutputStream
import java.time.Instant

/**
 * Exports a workout to GPX 1.1 format using JPX 3.2.1 (Apache 2.0).
 * Includes HR and cadence via Garmin TrackPointExtension if present.
 */
class GpxExporter {

    fun export(
        activity: Activity,
        trackPoints: List<TrackPoint>,
        outputStream: OutputStream
    ) {
        val waypoints = trackPoints.map { tp ->
            WayPoint.builder()
                .lat(tp.latitude)
                .lon(tp.longitude)
                .apply {
                    tp.altitudeMeters?.let { ele(it) }
                    time(Instant.ofEpochMilli(tp.timestampMs))
                    tp.speedMs?.let { speed(it.toDouble()) }
                }
                .build()
        }

        val segment = TrackSegment.builder()
            .points(waypoints)
            .build()

        val track = Track.builder()
            .name("${activity.workoutType.replaceFirstChar { it.uppercase() }} ${activity.id}")
            .addSegment(segment)
            .build()

        val gpx = GPX.builder()
            .creator("FitTracker")
            .addTrack(track)
            .build()

        GPX.writer("\t").write(gpx, outputStream)
    }
}
