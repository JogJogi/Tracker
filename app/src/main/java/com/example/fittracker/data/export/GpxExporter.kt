package com.example.fittracker.data.export

import android.util.Xml
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.TrackPoint
import java.io.OutputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Exports a workout to GPX 1.1 using Android's XmlSerializer.
 * No external library needed — avoids JPX API version fragility.
 * Supports Garmin TrackPointExtension v2 for HR and cadence.
 */
class GpxExporter {

    private val iso = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)

    fun export(
        activity: Activity,
        trackPoints: List<TrackPoint>,
        outputStream: OutputStream
    ) {
        val serializer = Xml.newSerializer()
        serializer.setOutput(outputStream, "UTF-8")
        serializer.startDocument("UTF-8", true)
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

        serializer.startTag(NS_GPX, "gpx")
        serializer.attribute("", "version", "1.1")
        serializer.attribute("", "creator", "FitTracker")
        serializer.attribute("", "xmlns", NS_GPX)
        serializer.attribute("", "xmlns:gpxtpx", NS_GPXTPX)

        serializer.startTag(NS_GPX, "trk")
        serializer.startTag(NS_GPX, "name")
        serializer.text("${activity.workoutType.replaceFirstChar { it.uppercase() }} ${activity.id}")
        serializer.endTag(NS_GPX, "name")

        serializer.startTag(NS_GPX, "trkseg")
        for (tp in trackPoints) {
            serializer.startTag(NS_GPX, "trkpt")
            serializer.attribute("", "lat", tp.latitude.toString())
            serializer.attribute("", "lon", tp.longitude.toString())

            tp.altitudeMeters?.let {
                serializer.startTag(NS_GPX, "ele")
                serializer.text("%.2f".format(it))
                serializer.endTag(NS_GPX, "ele")
            }

            serializer.startTag(NS_GPX, "time")
            serializer.text(iso.format(Instant.ofEpochMilli(tp.timestampMs)))
            serializer.endTag(NS_GPX, "time")

            if (tp.heartRateBpm != null || tp.cadenceRpm != null) {
                serializer.startTag(NS_GPX, "extensions")
                serializer.startTag(NS_GPXTPX, "TrackPointExtension")
                tp.heartRateBpm?.let {
                    serializer.startTag(NS_GPXTPX, "hr")
                    serializer.text(it.toString())
                    serializer.endTag(NS_GPXTPX, "hr")
                }
                tp.cadenceRpm?.let {
                    serializer.startTag(NS_GPXTPX, "cad")
                    serializer.text(it.toString())
                    serializer.endTag(NS_GPXTPX, "cad")
                }
                serializer.endTag(NS_GPXTPX, "TrackPointExtension")
                serializer.endTag(NS_GPX, "extensions")
            }

            serializer.endTag(NS_GPX, "trkpt")
        }
        serializer.endTag(NS_GPX, "trkseg")
        serializer.endTag(NS_GPX, "trk")
        serializer.endTag(NS_GPX, "gpx")
        serializer.endDocument()
    }

    companion object {
        private const val NS_GPX = "http://www.topografix.com/GPX/1/1"
        private const val NS_GPXTPX = "http://www.garmin.com/xmlschemas/TrackPointExtension/v2"
    }
}
