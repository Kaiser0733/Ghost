package com.ghost.blelab.util

import org.junit.Assert.*
import org.junit.Test

class TimeUtilTest {

    @Test
    fun testNowMillisReturnsPositiveValue() {
        val now = TimeUtil.nowMillis()
        assertTrue("Current time should be positive", now > 0)
    }

    @Test
    fun testNowNanosReturnsPositiveValue() {
        val now = TimeUtil.nowNanos()
        assertTrue("Current nanos should be positive", now > 0)
    }

    @Test
    fun testFormatDurationZero() {
        assertEquals("Zero duration", "0s", TimeUtil.formatDuration(0))
    }

    @Test
    fun testFormatDurationMilliseconds() {
        assertEquals("500ms", "500ms", TimeUtil.formatDuration(500))
    }

    @Test
    fun testFormatDurationSeconds() {
        assertEquals("5s", "5s", TimeUtil.formatDuration(5000))
    }

    @Test
    fun testFormatDurationMinutes() {
        assertEquals("2m 30s", "2m 30s", TimeUtil.formatDuration(2 * 60 * 1000 + 30 * 1000))
    }

    @Test
    fun testFormatDurationHours() {
        assertEquals("1h 30m 15s", "1h 30m 15s", TimeUtil.formatDuration(1 * 3600 * 1000 + 30 * 60 * 1000 + 15 * 1000))
    }

    @Test
    fun testFormatDurationDays() {
        assertEquals("1d 2h 3m 4s", "1d 2h 3m 4s", TimeUtil.formatDuration(1 * 24 * 3600 * 1000 + 2 * 3600 * 1000 + 3 * 60 * 1000 + 4 * 1000))
    }

    @Test
    fun testFormatTimestamp() {
        val timestamp = 1704067200000L // 2024-01-01T00:00:00Z
        val formatted = TimeUtil.formatTimestamp(timestamp)
        assertTrue("Should be valid ISO format", formatted.contains("2024-01-01"))
        assertTrue("Should have timezone", formatted.contains("Z") || formatted.contains("+00:00"))
    }

    @Test
    fun testParseTimestamp() {
        val isoString = "2024-01-01T00:00:00Z"
        val parsed = TimeUtil.parseTimestamp(isoString)
        assertEquals("Should parse to correct millis", 1704067200000L, parsed)
    }

    @Test
    fun testGetCurrentTimeSlot() {
        // Test with 10-minute intervals
        val slot = TimeUtil.getCurrentTimeSlot(10)
        assertTrue("Time slot should be non-negative", slot >= 0)
    }

    @Test
    fun testTimeSlotConsistency() {
        val intervalMinutes = 10
        val slot1 = TimeUtil.getCurrentTimeSlot(intervalMinutes)
        val slot2 = TimeUtil.getCurrentTimeSlot(intervalMinutes)
        assertEquals("Consecutive calls should return same slot", slot1, slot2)
    }

    @Test
    fun testGetTimeSlotStartMillis() {
        val intervalMinutes = 10
        val slot = 12345L
        val start = TimeUtil.getTimeSlotStartMillis(slot, intervalMinutes)
        val end = TimeUtil.getTimeSlotEndMillis(slot, intervalMinutes)
        
        assertEquals("Slot duration should be interval minutes", intervalMinutes.toLong() * 60 * 1000, end - start + 1)
    }

    @Test
    fun testMillisUntilNextRotation() {
        val intervalMinutes = 10
        val remaining = TimeUtil.millisUntilNextRotation(intervalMinutes)
        val maxMillis = intervalMinutes.toLong() * 60 * 1000
        
        assertTrue("Remaining should be positive", remaining > 0)
        assertTrue("Remaining should not exceed interval", remaining <= maxMillis)
    }

    @Test
    fun testFormatElapsedSince() {
        val start = System.currentTimeMillis() - 5000 // 5 seconds ago
        val elapsed = TimeUtil.formatElapsedSince(start)
        assertTrue("Should show elapsed time", elapsed.contains("s"))
    }

    @Test
    fun testNanosToMillis() {
        assertEquals("1 second in nanos", 1000L, TimeUtil.nanosToMillis(1_000_000_000L))
        assertEquals("500ms in nanos", 500L, TimeUtil.nanosToMillis(500_000_000L))
        assertEquals("0 nanos", 0L, TimeUtil.nanosToMillis(0L))
    }
}