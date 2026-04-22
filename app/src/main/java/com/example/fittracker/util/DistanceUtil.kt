package com.example.fittracker.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceUtil {

    private const val EARTH_RADIUS_M = 6_371_000.0

    /**
     * Haversine great-circle distance between two coordinates in meters.
     */
    fun haversineDistanceM(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (EARTH_RADIUS_M * c).toFloat()
    }

    /** Steps to km using average stride length derived from height. */
    fun stepsToKm(steps: Int, heightCm: Float): Float {
        val strideLengthM = heightCm * 0.00415f  // empirical coefficient
        return (steps * strideLengthM) / 1000f
    }

    /**
     * Calories burned via MET formula.
     * MET values: walking=3.5, running=8.0, cycling=6.0, hiking=5.3.
     */
    fun caloriesFromMet(met: Float, weightKg: Float, durationHours: Float): Int =
        (met * weightKg * durationHours).toInt()

    fun pace(distanceM: Float, durationMs: Long): Float {
        if (distanceM == 0f || durationMs == 0L) return 0f
        return (durationMs / 1000f / 60f) / (distanceM / 1000f) // min/km
    }
}
