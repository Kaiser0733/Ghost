package com.ghost.blelab.ble.scanner

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.bluetooth.le.ScanResult
import android.util.Log

class ScannerBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SCAN_RESULTS = "com.ghost.blelab.SCAN_RESULTS"
        const val EXTRA_SCAN_RESULTS = "scan_results"
        const val EXTRA_ERROR_CODE = "error_code"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SCAN_RESULTS) return

        // Handle batched scan results
        if (intent.hasExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)) {
            val results = intent.getParcelableArrayListExtra<ScanResult>(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)
            if (results != null && results.isNotEmpty()) {
                Log.d("ScannerBroadcastReceiver", "Received ${results.size} batched scan results")
                // Process results through the scanner callback
                ScannerCallbackHolder.callbackRef?.let { callback ->
                    results.forEach { callback.onScanResult(it) }
                    callback.onBatchScanResults(results)
                }
            }
        } else if (intent.hasExtra(BluetoothLeScanner.EXTRA_SCAN_RESULT)) {
            // Single scan result
            val result = intent.getParcelableExtra<ScanResult>(BluetoothLeScanner.EXTRA_SCAN_RESULT)
            result?.let {
                Log.d("ScannerBroadcastReceiver", "Received single scan result")
                ScannerCallbackHolder.callbackRef?.onScanResult(it)
            }
        } else if (intent.hasExtra(BluetoothLeScanner.EXTRA_ERROR_CODE)) {
            // Scan error
            val errorCode = intent.getIntExtra(BluetoothLeScanner.EXTRA_ERROR_CODE, -1)
            Log.e("ScannerBroadcastReceiver", "Scan error: $errorCode")
            ScannerCallbackHolder.callbackRef?.onScanFailed(errorCode)
        }
    }
}

/**
 * Holder for the scanner callback reference.
 * This is a workaround since we can't easily pass the callback to a BroadcastReceiver.
 */
object ScannerCallbackHolder {
    var callbackRef: com.ghost.blelab.ble.scanner.BleScanner.ScanCallback? = null
}