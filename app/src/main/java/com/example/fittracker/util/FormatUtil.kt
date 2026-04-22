package com.example.fittracker.util

object FormatUtil {

    fun formatDistanceKm(meters: Float, useMetric: Boolean = true): String =
        if (useMetric) "%.2f km".format(meters / 1000f)
        else "%.2f mi".format(meters / 1609.344f)

    fun formatDuration(ms: Long): String {
        val seconds = ms / 1000
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }

    fun formatPace(minPerKm: Float): String {
        if (minPerKm <= 0f || minPerKm.isInfinite() || minPerKm.isNaN()) return "--:--"
        val totalSec = (minPerKm * 60).toInt()
        val m = totalSec / 60
        val s = totalSec % 60
        return "%d:%02d /km".format(m, s)
    }

    fun formatSpeed(ms: Float, useMetric: Boolean = true): String =
        if (useMetric) "%.1f km/h".format(ms * 3.6f)
        else "%.1f mph".format(ms * 2.237f)

    fun formatCalories(kcal: Int): String = "$kcal kcal"

    fun formatHeartRate(bpm: Int?): String = if (bpm != null) "$bpm bpm" else "-- bpm"

    fun formatSteps(steps: Int): String = "%,d".format(steps)

    fun formatWeight(kg: Float, useMetric: Boolean = true): String =
        if (useMetric) "%.1f kg".format(kg)
        else "%.1f lbs".format(kg * 2.205f)
}
