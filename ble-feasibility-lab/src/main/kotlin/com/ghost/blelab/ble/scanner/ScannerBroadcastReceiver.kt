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

        // Android BluetoothLeScanner extra keys (available since API 26)
        // Use string literals to avoid SDK version issues
        private const val EXTRA_LIST_SCAN_RESULT = "android.bluetooth.le.extra.LIST_SCAN_RESULT"
        private const val EXTRA_SCAN_RESULT = "android.bluetooth.le.extra.SCAN_RESULT"
        private const val EXTRA_ERROR_CODE_KEY = "android.bluetooth.le.extra.ERROR_CODE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_SCAN_RESULTS) return

        // Handle batched scan results
        if (intent.hasExtra(EXTRA_LIST_SCAN_RESULT)) {
            val results = intent.getParcelableArrayListExtra<ScanResult>(EXTRA_LIST_SCAN_RESULT)
            if (results != null && results.isNotEmpty()) {
                Log.d("ScannerBroadcastReceiver", "Received ${results.size} batched scan results")
                ScannerCallbackHolder.callbackRef?.let { callback ->
                    results.forEach { callback.onScanResult(it) }
                    callback.onBatchScanResults(results)
                }
            }
        } else if (intent.hasExtra(EXTRA_SCAN_RESULT)) {
            // Single scan result
            val result = intent.getParcelableExtra<ScanResult>(EXTRA_SCAN_RESULT)
            result?.let {
                Log.d("ScannerBroadcastReceiver", "Received single scan result")
                ScannerCallbackHolder.callbackRef?.onScanResult(it)
            }
        } else if (intent.hasExtra(EXTRA_ERROR_CODE_KEY)) {
            // Scan error
            val errorCode = intent.getIntExtra(EXTRA_ERROR_CODE_KEY, -1)
            Log.e("ScannerBroadcastReceiver", "Scan error: $errorCode")
            ScannerCallbackHolder.callbackRef?.onScanFailed(errorCode)
        }
    }
}

/**
 * Holder for the scanner callback reference.
 */
object ScannerCallbackHolder {
    var callbackRef: com.ghost.blelab.ble.scanner.BleScanner.ScanCallback? = null
}