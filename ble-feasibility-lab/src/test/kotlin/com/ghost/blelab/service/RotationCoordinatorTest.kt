package com.ghost.blelab.service

import com.ghost.blelab.ephemeral.TimeSlotCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [RotationCoordinator] — the pure timing/state logic behind
 * Phase F's ephemeral-ID rotation.
 *
 * These tests verify the state machine only. They do NOT verify Android
 * foreground-service behavior (that requires a device — PHYSICALLY UNVERIFIED).
 */
class RotationCoordinatorTest {

    /** Deterministic fake: time slot and remaining millis are set by the test. */
    private class FakeTimeSlotCalculator(
        var slot: Long = 0L,
        var remainingMillis: Long = 60_000L
    ) : TimeSlotCalculator {
        override fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long = slot
        override fun getTimeSlotStartMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long =
            timeSlot * rotationIntervalMinutes * 60_000L
        override fun getTimeSlotEndMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long =
            getTimeSlotStartMillis(timeSlot, rotationIntervalMinutes) + rotationIntervalMinutes * 60_000L - 1
        override fun millisUntilNextRotation(rotationIntervalMinutes: Int): Long = remainingMillis
    }

    private fun coordinator(
        fake: FakeTimeSlotCalculator,
        derive: (Long) -> ByteArray = { slot -> ByteArray(16) { slot.toByte() } }
    ) = RotationCoordinator(fake, rotationIntervalMinutes = 10, deriveEphemeralId = derive)

    @Test
    fun `first action is rotate for current slot`() {
        val fake = FakeTimeSlotCalculator(slot = 42L)
        val coordinator = coordinator(fake)

        val action = coordinator.nextAction()

        assertTrue("First action must be Rotate", action is RotationCoordinator.Action.Rotate)
        action as RotationCoordinator.Action.Rotate
        assertEquals(42L, action.timeSlot)
        assertEquals(ByteArray(16) { 42.toByte() }.toList(), action.ephemeralId.toList())
    }

    @Test
    fun `unconfirmed rotation is retried not skipped`() {
        val fake = FakeTimeSlotCalculator(slot = 7L)
        val coordinator = coordinator(fake)

        val first = coordinator.nextAction()
        // No confirmRotation call — simulates a failed BLE update.
        val second = coordinator.nextAction()

        assertTrue(first is RotationCoordinator.Action.Rotate)
        assertTrue(second is RotationCoordinator.Action.Rotate)
        assertEquals((first as RotationCoordinator.Action.Rotate).timeSlot,
            (second as RotationCoordinator.Action.Rotate).timeSlot)
    }

    @Test
    fun `confirmed rotation waits until slot advances`() {
        val fake = FakeTimeSlotCalculator(slot = 7L, remainingMillis = 123_456L)
        val coordinator = coordinator(fake)

        val rotate = coordinator.nextAction() as RotationCoordinator.Action.Rotate
        coordinator.confirmRotation(rotate.timeSlot)

        val wait = coordinator.nextAction()
        assertTrue("After confirm, action must be Wait", wait is RotationCoordinator.Action.Wait)
        assertEquals(123_456L, (wait as RotationCoordinator.Action.Wait).delayMillis)
    }

    @Test
    fun `wait delay comes from TimeSlotCalculator not an independent clock`() {
        val fake = FakeTimeSlotCalculator(slot = 3L, remainingMillis = 999L)
        val coordinator = coordinator(fake)
        coordinator.confirmRotation(3L)

        val wait = coordinator.nextAction() as RotationCoordinator.Action.Wait
        assertEquals("Delay must equal TimeSlotCalculator.millisUntilNextRotation",
            999L, wait.delayMillis)

        // Advance the calculator's remaining time; delay must follow it.
        fake.remainingMillis = 555L
        val wait2 = coordinator.nextAction() as RotationCoordinator.Action.Wait
        assertEquals(555L, wait2.delayMillis)
    }

    @Test
    fun `slot advance triggers new rotation with derived id`() {
        val fake = FakeTimeSlotCalculator(slot = 10L)
        val coordinator = coordinator(fake)

        val first = coordinator.nextAction() as RotationCoordinator.Action.Rotate
        coordinator.confirmRotation(first.timeSlot)

        fake.slot = 11L
        val second = coordinator.nextAction()
        assertTrue("Slot advance must trigger Rotate", second is RotationCoordinator.Action.Rotate)
        second as RotationCoordinator.Action.Rotate
        assertEquals(11L, second.timeSlot)
        assertEquals(ByteArray(16) { 11.toByte() }.toList(), second.ephemeralId.toList())
    }

    @Test
    fun `slot jump across multiple intervals rotates once to current slot`() {
        val fake = FakeTimeSlotCalculator(slot = 10L)
        val coordinator = coordinator(fake)
        coordinator.confirmRotation(10L)

        fake.slot = 15L // e.g. device slept across several rotation boundaries
        val action = coordinator.nextAction() as RotationCoordinator.Action.Rotate
        assertEquals(15L, action.timeSlot)
    }

    @Test
    fun `lastRotatedTimeSlot reflects confirmations`() {
        val fake = FakeTimeSlotCalculator(slot = 1L)
        val coordinator = coordinator(fake)

        assertNull(coordinator.lastRotatedTimeSlot())
        coordinator.confirmRotation(1L)
        assertEquals(1L, coordinator.lastRotatedTimeSlot())
    }
}
