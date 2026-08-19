package com.ghost.blelab.service

import com.ghost.blelab.experiment.DeviceState
import com.ghost.blelab.experiment.Environment
import com.ghost.blelab.experiment.ExperimentConfig
import com.ghost.blelab.experiment.Role
import com.ghost.blelab.experiment.TestCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [ExperimentStartConfigMerger] — the pure rule that merges a
 * UI-selected pending test condition into the start config.
 */
class ExperimentStartConfigMergerTest {

    @Test
    fun `null pending condition returns config unchanged and does not consume`() {
        val config = ExperimentConfig(role = Role.ADVERTISER)
        var consumed = false

        val result = ExperimentStartConfigMerger.merge(
            config = config,
            pendingCondition = null,
            onConsumed = { consumed = true }
        )

        assertSame(config, result)
        assertFalse("onConsumed must not fire without a pending condition", consumed)
    }

    @Test
    fun `pending condition is applied and consumed exactly once`() {
        val config = ExperimentConfig(role = Role.SCANNER)
        val condition = TestCondition(
            distanceMeters = 5,
            environment = Environment.APARTMENT_WALL,
            deviceState = DeviceState.LOCKED
        )
        var consumeCount = 0

        val result = ExperimentStartConfigMerger.merge(
            config = config,
            pendingCondition = condition,
            onConsumed = { consumeCount++ }
        )

        assertEquals(condition, result.testCondition)
        assertEquals("onConsumed must fire exactly once", 1, consumeCount)
        // Other config fields preserved
        assertEquals(Role.SCANNER, result.role)
        assertEquals(config.rotationIntervalMinutes, result.rotationIntervalMinutes)
        assertEquals(config.txPowerLevel, result.txPowerLevel)
    }

    @Test
    fun `pending condition replaces existing default condition`() {
        val config = ExperimentConfig(role = Role.ADVERTISER)
        assertTrue(config.testCondition == TestCondition.UNSPECIFIED)

        val condition = TestCondition(distanceMeters = 1)
        val result = ExperimentStartConfigMerger.merge(config, condition) {}

        assertEquals(1, result.testCondition.distanceMeters)
    }
}
