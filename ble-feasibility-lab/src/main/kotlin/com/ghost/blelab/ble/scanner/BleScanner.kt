package com.ghost.blelab.ble.scanner

import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.app.PendingIntent
import android.bluetooth.le.ScanFilter
import kotlin.Result

/**
 * Interface for BLE scanning with experimental service UUID filtering.
 * 
 * The scanner handles:
 * - Starting/stopping BLE scans with service UUID filter
 * - PendingIntent-based background scanning
 * - Batch scan results for screen-off operation
 * - Error handling for BLE state changes
 */
interface BleScanner {

    /**
     * Start scanning with the given filters and settings using PendingIntent.
     * Returns success or failure with error details.
     */
    fun startScanning(filters: List<ScanFilter>, settings: ScanSettings, pendingIntent: PendingIntent): Result<Unit>

    /**
     * Stop scanning for the given PendingIntent.
     * Returns success or failure with error details.
     */
    fun stopScanning(pendingIntent: PendingIntent): Result<Unit>

    /**
     * Set callback for scan events.
     */
    fun setCallback(callback: BleScanner.ScanCallback?)

    /**
     * Current scanning state.
     */
    fun isScanning(): Boolean

    /**
     * Callback for scanning lifecycle events.
     * Implemented as interface with default methods.
     */
    interface ScanCallback {
        fun onScanResult(result: ScanResult) {}
        fun onBatchScanResults(results: List<ScanResult>) {}
        fun onScanFailed(errorCode: Int) {}
    }
}