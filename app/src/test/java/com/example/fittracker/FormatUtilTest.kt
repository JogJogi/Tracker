package com.example.fittracker

import com.example.fittracker.util.FormatUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilTest {

    @Test
    fun `formatDuration seconds only`() {
        assertEquals("00:45", FormatUtil.formatDuration(45_000L))
    }

    @Test
    fun `formatDuration minutes and seconds`() {
        assertEquals("05:30", FormatUtil.formatDuration(330_000L))
    }

    @Test
    fun `formatDuration with hours`() {
        assertEquals("1:02:03", FormatUtil.formatDuration(3_723_000L))
    }

    @Test
    fun `formatPace zero returns placeholder`() {
        assertEquals("--:--", FormatUtil.formatPace(0f))
    }

    @Test
    fun `formatPace five minutes per km`() {
        assertEquals("5:00 /km", FormatUtil.formatPace(5f))
    }

    @Test
    fun `formatDistanceKm metric`() {
        assertEquals("5.00 km", FormatUtil.formatDistanceKm(5000f, useMetric = true))
    }

    @Test
    fun `formatSteps adds thousands separator`() {
        val result = FormatUtil.formatSteps(10000)
        assertEquals("10,000", result)
    }
}
