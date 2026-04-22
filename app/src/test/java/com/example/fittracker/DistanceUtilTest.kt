package com.example.fittracker

import com.example.fittracker.util.DistanceUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class DistanceUtilTest {

    @Test
    fun `haversine distance between same point is zero`() {
        val dist = DistanceUtil.haversineDistanceM(48.8566, 2.3522, 48.8566, 2.3522)
        assertEquals(0f, dist, 0.001f)
    }

    @Test
    fun `haversine distance Berlin to Munich is approximately 504 km`() {
        // Berlin: 52.52, 13.405 — Munich: 48.137, 11.575
        val dist = DistanceUtil.haversineDistanceM(52.52, 13.405, 48.137, 11.575)
        val km = dist / 1000f
        assertTrue("Expected ~504 km but got $km km", abs(km - 504f) < 5f)
    }

    @Test
    fun `pace calculation`() {
        // 5000m in 25 minutes = 5 min/km
        val pace = DistanceUtil.pace(5000f, 25 * 60 * 1000L)
        assertEquals(5f, pace, 0.05f)
    }

    @Test
    fun `calories from MET walking`() {
        // MET 3.5, 70kg, 1 hour = 245 kcal
        val kcal = DistanceUtil.caloriesFromMet(3.5f, 70f, 1f)
        assertEquals(245, kcal)
    }

    @Test
    fun `steps to km with average height`() {
        // 10000 steps, 170 cm => ~7.055 km
        val km = DistanceUtil.stepsToKm(10000, 170f)
        assertTrue("Expected ~7 km, got $km", abs(km - 7.055f) < 0.1f)
    }
}
