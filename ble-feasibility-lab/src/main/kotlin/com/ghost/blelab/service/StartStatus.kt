package com.ghost.blelab.service

/**
 * Observable start status for the BLE experiment foreground service.
 *
 * The service runs in its own Android component; without this channel every
 * start failure (Bluetooth off, permission missing, scan registration
 * failed) was only visible in logcat while the UI showed "Stopped" with no
 * explanation. [BleExperimentService] publishes transitions; the UI
 * observes [BleExperimentService.startStatus].
 *
 * Top-level (not nested in the service's companion object) so it can be
 * referenced unambiguously from any file.
 */
sealed class StartStatus {
    /** No start attempted in this process yet. */
    object Idle : StartStatus()

    /** Start requested; waiting for foreground + BLE startup. */
    object Starting : StartStatus()

    /** Experiment running. */
    object Running : StartStatus()

    /** Start failed with an actionable reason. */
    data class Failed(val reason: String) : StartStatus()

    /** Experiment stopped normally. */
    object Stopped : StartStatus()
}
