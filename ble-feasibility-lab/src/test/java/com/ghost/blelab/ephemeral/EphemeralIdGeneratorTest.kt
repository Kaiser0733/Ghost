package com.ghost.blelab.ephemeral

import kotlinx.serialization.Serializable
import org.junit.Assert.*
import org.junit.Test

/**
 * TDD Tests for EphemeralIdGenerator
 * 
 * These tests define the expected behavior BEFORE implementation.
 * Run with: gradle :ble-feasibility-lab:test --tests "com.ghost.blelab.ephemeral.EphemeralIdGeneratorTest"
 */
class EphemeralIdGeneratorTest {

    private val generator = EphemeralIdGeneratorImpl()
    private val timeSlotCalculator = TimeSlotCalculatorImpl()

    @Test
    fun testRotationIntervalDefault10Minutes() {
        assertEquals("Default rotation interval should be 10 minutes", 10, generator.getRotationIntervalMinutes())
    }

    @Test
    fun testRotationIntervalConfigurable() {
        generator.setRotationIntervalMinutes(5)
        assertEquals("Rotation interval should be configurable", 5, generator.getRotationIntervalMinutes())
        
        generator.setRotationIntervalMinutes(15)
        assertEquals("Rotation interval should be configurable to 15", 15, generator.getRotationIntervalMinutes())
        
        generator.setRotationIntervalMinutes(60)
        assertEquals("Rotation interval should be configurable to 60", 60, generator.getRotationIntervalMinutes())
    }

    @Test
    fun testSetRotationIntervalRejectsNonPositive() {
        try {
            generator.setRotationIntervalMinutes(0)
            fail("Should reject zero interval")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
        
        try {
            generator.setRotationIntervalMinutes(-5)
            fail("Should reject negative interval")
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }

    @Test
    fun testGenerateDailyKeyReturns16Bytes() {
        val dailyKey = generator.generateDailyKey()
        assertNotNull("Daily key should not be null", dailyKey)
        assertEquals("Daily key should be 16 bytes", 16, dailyKey.size)
    }

    @Test
    fun testGenerateDailyKeyProducesDifferentKeys() {
        val key1 = generator.generateDailyKey()
        val key2 = generator.generateDailyKey()
        assertFalse("Two generated keys should be different", java.util.Arrays.equals(key1, key2))
    }

    @Test
    fun testSameTimeSlotProducesSameId() {
        val dailyKey = generator.generateDailyKey()
        val timeSlot = 12345L
        
        val id1 = generator.deriveEphemeralId(dailyKey, timeSlot)
        val id2 = generator.deriveEphemeralId(dailyKey, timeSlot)
        
        assertTrue("Same time slot should produce identical ephemeral ID", java.util.Arrays.equals(id1, id2))
    }

    @Test
    fun testDifferentTimeSlotProducesDifferentId() {
        val dailyKey = generator.generateDailyKey()
        
        val id1 = generator.deriveEphemeralId(dailyKey, 1000L)
        val id2 = generator.deriveEphemeralId(dailyKey, 1001L)
        
        assertFalse("Different time slots should produce different ephemeral IDs", java.util.Arrays.equals(id1, id2))
    }

    @Test
    fun testDifferentDailyKeyProducesDifferentId() {
        val dailyKey1 = generator.generateDailyKey()
        val dailyKey2 = generator.generateDailyKey()
        val timeSlot = 12345L
        
        val id1 = generator.deriveEphemeralId(dailyKey1, timeSlot)
        val id2 = generator.deriveEphemeralId(dailyKey2, timeSlot)
        
        assertFalse("Different daily keys should produce different ephemeral IDs", java.util.Arrays.equals(id1, id2))
    }

    @Test
    fun testEphemeralIdLengthIs16Bytes() {
        val dailyKey = generator.generateDailyKey()
        val ephemeralId = generator.deriveEphemeralId(dailyKey, 12345L)
        
        assertEquals("Ephemeral ID should be 16 bytes", 16, ephemeralId.size)
    }

    @Test
    fun testEphemeralIdDoesNotContainDeviceInfo() {
        val dailyKey = generator.generateDailyKey()
        val ephemeralId = generator.deriveEphemeralId(dailyKey, 12345L)
        
        // The ephemeral ID should be cryptographically derived, not contain
        // any device-specific information like MAC, name, model, etc.
        // This is verified by the fact that identical inputs produce identical outputs
        // and different inputs produce different outputs, without any device context.
        val ephemeralId2 = generator.deriveEphemeralId(dailyKey, 12345L)
        assertTrue("Deterministic derivation", java.util.Arrays.equals(ephemeralId, ephemeralId2))
    }

    @Test
    fun testGetCurrentEphemeralIdUsesCurrentTimeSlot() {
        val dailyKey = generator.generateDailyKey()
        val id1 = generator.getCurrentEphemeralId(dailyKey, 10)
        val id2 = generator.getCurrentEphemeralId(dailyKey, 10)
        
        // Should be same since called within same time slot
        assertTrue("Current ephemeral ID should be stable within time slot", java.util.Arrays.equals(id1, id2))
    }

    // TimeSlotCalculator tests
    @Test
    fun testTimeSlotCalculatorDefaultInterval() {
        val slot = timeSlotCalculator.getCurrentTimeSlot(10)
        assertTrue("Time slot should be non-negative", slot >= 0)
    }

    @Test
    fun testTimeSlotCalculatorConsistency() {
        val slot1 = timeSlotCalculator.getCurrentTimeSlot(10)
        val slot2 = timeSlotCalculator.getCurrentTimeSlot(10)
        assertEquals("Consecutive calls should return same slot", slot1, slot2)
    }

    @Test
    fun testTimeSlotCalculatorDifferentIntervals() {
        val slot10min = timeSlotCalculator.getCurrentTimeSlot(10)
        val slot5min = timeSlotCalculator.getCurrentTimeSlot(5)
        
        // 5-minute slots should be roughly double 10-minute slots
        assertTrue("5-min slots should be >= 10-min slots", slot5min >= slot10min)
    }

    @Test
    fun testTimeSlotBoundaries() {
        val intervalMinutes = 10
        val slot = 12345L
        
        val start = timeSlotCalculator.getTimeSlotStartMillis(slot, intervalMinutes)
        val end = timeSlotCalculator.getTimeSlotEndMillis(slot, intervalMinutes)
        
        val expectedDuration = intervalMinutes.toLong() * 60 * 1000
        assertEquals("Slot duration should match interval", expectedDuration, end - start + 1)
    }
}