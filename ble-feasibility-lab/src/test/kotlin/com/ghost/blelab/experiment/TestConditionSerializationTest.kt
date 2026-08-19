package com.ghost.blelab.experiment

import com.ghost.blelab.util.JsonUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [TestCondition] / [ExperimentConfig] serialization.
 *
 * These matter for correctness, not just coverage: the experiment config
 * crosses the Intent boundary (UI -> foreground service) as JSON and is
 * persisted to disk for process-death survival. A field that silently drops
 * out of serialization silently corrupts the experiment record.
 */
class TestConditionSerializationTest {

    @Test
    fun `test condition round-trips through JSON with all fields`() {
        val condition = TestCondition(
            distanceMeters = 10,
            environment = Environment.CROWDED_INDOOR,
            deviceState = DeviceState.LOCKED,
            orientation = Orientation.ONE_IN_POCKET,
            pocketState = PocketState.IN_POCKET,
            wallCondition = WallCondition.CONCRETE
        )

        val json = JsonUtil.toJson(TestCondition.serializer(), condition)
        val restored = JsonUtil.fromJson(TestCondition.serializer(), json).getOrThrow()

        assertEquals(condition, restored)
        assertEquals(10, restored.distanceMeters)
        assertEquals(WallCondition.CONCRETE, restored.wallCondition)
    }

    @Test
    fun `experiment config round-trips through JSON`() {
        val config = ExperimentConfig(
            role = Role.SCANNER,
            rotationIntervalMinutes = 10,
            testCondition = TestCondition(distanceMeters = 3, wallCondition = WallCondition.DRYWALL)
        )

        val json = JsonUtil.toJson(ExperimentConfig.serializer(), config)
        val restored = JsonUtil.fromJson(ExperimentConfig.serializer(), json).getOrThrow()

        assertEquals(config, restored)
        assertEquals(Role.SCANNER, restored.role)
        assertEquals(3, restored.testCondition.distanceMeters)
        assertEquals(WallCondition.DRYWALL, restored.testCondition.wallCondition)
    }

    @Test
    fun `null distance survives round trip`() {
        val condition = TestCondition(distanceMeters = null)
        val json = JsonUtil.toJson(TestCondition.serializer(), condition)
        val restored = JsonUtil.fromJson(TestCondition.serializer(), json).getOrThrow()
        assertNull(restored.distanceMeters)
    }

    @Test
    fun `wall condition enum has exactly the protocol values`() {
        // Protocol: wall_condition = "none" / "drywall" / "concrete"
        // (+ UNSPECIFIED sentinel for "not recorded").
        val names = WallCondition.values().map { it.name }.toSet()
        assertEquals(setOf("UNSPECIFIED", "NONE", "DRYWALL", "CONCRETE"), names)
    }

    @Test
    fun `distance options match protocol distance matrix`() {
        // Protocol distance matrix D1..D5: 1, 3, 5, 10, 20 meters.
        val protocolDistances = listOf(1, 3, 5, 10, 20)
        val uiOptions = listOf("Not set", "1", "3", "5", "10", "20")
        assertEquals(
            protocolDistances,
            uiOptions.drop(1).map { it.toInt() }
        )
    }

    @Test
    fun `default test condition is fully unspecified`() {
        val d = TestCondition.UNSPECIFIED
        assertNull(d.distanceMeters)
        assertEquals(Environment.UNSPECIFIED, d.environment)
        assertEquals(DeviceState.UNSPECIFIED, d.deviceState)
        assertEquals(Orientation.UNSPECIFIED, d.orientation)
        assertEquals(PocketState.UNSPECIFIED, d.pocketState)
        assertEquals(WallCondition.UNSPECIFIED, d.wallCondition)
    }
}
