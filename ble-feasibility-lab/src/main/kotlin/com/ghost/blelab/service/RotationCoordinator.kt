package com.ghost.blelab.service

import com.ghost.blelab.ephemeral.TimeSlotCalculator

/**
 * Pure timing/state logic for ephemeral-ID rotation inside [BleExperimentService].
 *
 * This class deliberately owns NO clock and NO timer. Every timing decision is
 * derived from the existing [TimeSlotCalculator], so service-side rotation can
 * never drift from the time-slot system used by the ephemeral-ID generator.
 * The service's coroutine loop only supplies the "sleep" between decisions.
 *
 * Framework-free and unit-testable: inject a fake [TimeSlotCalculator] to
 * control time in tests.
 */
class RotationCoordinator(
    private val timeSlotCalculator: TimeSlotCalculator,
    private val rotationIntervalMinutes: Int,
    private val deriveEphemeralId: (timeSlot: Long) -> ByteArray,
) {

    /**
     * The next action the service should perform.
     */
    sealed class Action {
        /** No rotation due yet; sleep for [delayMillis] and ask again. */
        data class Wait(val delayMillis: Long) : Action()

        /** Rotate now to the ephemeral ID for [timeSlot]. */
        data class Rotate(val timeSlot: Long, val ephemeralId: ByteArray) : Action()
    }

    private var lastRotatedSlot: Long? = null

    /**
     * Compute the next rotation action from the current time slot.
     *
     * - Never rotated in this run -> Rotate(current slot).
     * - Current slot advanced past the last confirmed rotation -> Rotate(current slot).
     * - Otherwise -> Wait(remaining millis in the current slot, per TimeSlotCalculator).
     */
    fun nextAction(): Action {
        val currentSlot = timeSlotCalculator.getCurrentTimeSlot(rotationIntervalMinutes)
        val last = lastRotatedSlot
        return if (last == null || currentSlot > last) {
            Action.Rotate(currentSlot, deriveEphemeralId(currentSlot))
        } else {
            Action.Wait(timeSlotCalculator.millisUntilNextRotation(rotationIntervalMinutes))
        }
    }

    /**
     * Confirm that a rotation to [timeSlot] was applied successfully.
     * Until confirmed, [nextAction] keeps returning the same Rotate, so a
     * failed BLE update is retried instead of silently skipped.
     */
    fun confirmRotation(timeSlot: Long) {
        lastRotatedSlot = timeSlot
    }

    /** Last time slot for which rotation was confirmed, if any. */
    fun lastRotatedTimeSlot(): Long? = lastRotatedSlot
}
