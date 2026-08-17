package com.ghost.blelab.ble.advertiser

import android.bluetooth.le.AdvertiseSettings
import kotlin.Result

/**
 * Interface for BLE advertising with rotating ephemeral identifiers.
 * 
 * The advertiser handles:
 * - Starting/stopping BLE advertisement with service data
 * - Rotating ephemeral IDs based on time slots
 * - TX power configuration
 * - Error handling for BLE state changes
 */
interface BleAdvertiser {

    /**
     * Start advertising with the given ephemeral ID and TX power.
     * Returns success or failure with error details.
     */
    fun startAdvertising(ephemeralId: ByteArray, txPowerLevel: Int): Result<Unit>

    /**
     * Stop advertising.
     * Returns success or failure with error details.
     */
    fun stopAdvertising(): Result<Unit>

    /**
     * Update the ephemeral ID being advertised (for rotation).
     * If currently advertising, restarts with new ID.
     */
    fun updateEphemeralId(newEphemeralId: ByteArray): Result<Unit>

    /**
     * Set the TX power level for future advertisements.
     */
    fun setTxPowerLevel(txPowerLevel: Int)

    /**
     * Current advertising state.
     */
    fun isAdvertising(): Boolean

    /**
     * Callback for advertising lifecycle events.
     */
    sealed interface AdvertisingCallback {
        data class OnStartSuccess(val settings: AdvertiseSettings) : AdvertisingCallback
        data class OnStartFailure(val errorCode: Int) : AdvertisingCallback
        object OnStopSuccess : AdvertisingCallback
        data class OnStopFailure(val errorCode: Int) : AdvertisingCallback
        data class OnRotationUpdated(val newEphemeralId: ByteArray) : AdvertisingCallback
    }

    /**
     * Set callback for advertising events.
     */
    fun setCallback(callback: AdvertisingCallback?)
}