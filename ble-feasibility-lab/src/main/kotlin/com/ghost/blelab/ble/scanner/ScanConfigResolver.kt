package com.ghost.blelab.ble.scanner

/**
 * Pure-JVM resolution of BLE scan configuration.
 *
 * Exists because two physical test devices rejected the previous hardcoded
 * ScanSettings at start time (framework-side validation throws):
 *
 *  - Galaxy A03 (Android 13 / API 33):
 *      "invalid callback type-8"
 *      CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH (=8) does not exist in the API 33
 *      framework; it was added in Android 14 (API 34). The API 33
 *      ScanSettings.Builder.isValidCallbackType() only accepts 1, 2, 4, and 6.
 *
 *  - Galaxy Tab A9+ (Android 16 / API 36):
 *      "report delay for auto batch must be >= 600000"
 *      On API 34+ the constant exists, but ScanSettings.Builder.build()
 *      enforces reportDelayMillis >= AUTO_BATCH_MIN_REPORT_DELAY_MILLIS
 *      (600000 = 10 minutes) whenever callbackType == AUTO_BATCH. The old
 *      code never called setReportDelay(), so reportDelayMillis defaulted
 *      to 0 and build() threw.
 *
 * Verified against AOSP framework source:
 *   packages/modules/Bluetooth framework/java/android/bluetooth/le/ScanSettings.java
 *   tags android-13.0.0_r1, android-14.0.0_r1, android-16.0.0_r1
 *
 * Resolution: a single unified configuration valid on every API level in the
 * test fleet (33 and 36): CALLBACK_TYPE_ALL_MATCHES with reportDelayMillis 0.
 * Using one configuration on both devices keeps detection-latency and
 * delivery-cadence measurements comparable when roles are swapped across the
 * pair (protocol section 5). AUTO_BATCH is deliberately NOT used: it is
 * impossible on API 33, and on API 34+ its mandatory >=10-minute batch
 * cadence would destroy the 60-second detection-rate windows in states S3-S6.
 *
 * Documented methodology change (see DECISIONS.md DECISION-008): the
 * framework-guaranteed screen-off batch cadence is dropped. Screen-off
 * behavior now depends on the foreground service + PendingIntent broadcast
 * delivery rather than a framework batch timer. All recorded observables
 * (RSSI, timestampNanos, ephemeral ID, service-UUID filter) are unchanged.
 */
object ScanConfigResolver {

    // Mirrors android.bluetooth.le.ScanSettings constants. Duplicated as plain
    // ints so this logic is unit-testable on the JVM without Android classes.
    const val CALLBACK_TYPE_ALL_MATCHES = 1
    const val CALLBACK_TYPE_FIRST_MATCH = 2
    const val CALLBACK_TYPE_MATCH_LOST = 4
    const val CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH = 8
    const val CALLBACK_TYPE_FIRST_MATCH_AND_MATCH_LOST =
        CALLBACK_TYPE_FIRST_MATCH or CALLBACK_TYPE_MATCH_LOST // 6

    /** Framework-enforced minimum report delay for AUTO_BATCH (API 34+). */
    const val AUTO_BATCH_MIN_REPORT_DELAY_MILLIS = 600_000L

    /** API level at which CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH exists. */
    const val AUTO_BATCH_MIN_API = 34

    data class ResolvedScanConfig(
        val callbackType: Int,
        val reportDelayMillis: Long,
        val usesAutoBatch: Boolean,
        val rationale: String,
    )

    /**
     * Resolve the scanner configuration for a device running [sdkInt].
     *
     * Returns the same unified configuration on every API level: immediate
     * per-advertisement delivery via CALLBACK_TYPE_ALL_MATCHES with
     * reportDelayMillis 0. This is the only configuration that is valid on
     * BOTH test devices (API 33 and API 36) without changing the experiment
     * methodology between them.
     */
    fun resolve(sdkInt: Int): ResolvedScanConfig {
        require(sdkInt >= 21) { "BLE scanning requires API 21+, got $sdkInt" }
        return ResolvedScanConfig(
            callbackType = CALLBACK_TYPE_ALL_MATCHES,
            reportDelayMillis = 0L,
            usesAutoBatch = false,
            rationale = "Unified config valid on API $sdkInt: ALL_MATCHES + " +
                "reportDelay 0. AUTO_BATCH rejected: unavailable below API " +
                "$AUTO_BATCH_MIN_API and forces >=10min delivery cadence above it.",
        )
    }

    /**
     * Validate a (callbackType, reportDelayMillis) pair the way the Android
     * framework's ScanSettings.Builder does on a device running [sdkInt].
     *
     * Returns null if the framework would accept the pair, or the exact
     * IllegalArgumentException message the framework would throw. Mirrors:
     *  - API 33:  isValidCallbackType() accepts only 1, 2, 4, 6
     *  - API 34+: isValidCallbackType() also accepts 8; build() additionally
     *             requires reportDelayMillis >= 600000 when callbackType == 8
     *  - All APIs: reportDelayMillis < 0 is rejected
     */
    fun validate(callbackType: Int, reportDelayMillis: Long, sdkInt: Int): String? {
        if (reportDelayMillis < 0) {
            return "reportDelay must be > 0"
        }
        val validTypes = if (sdkInt >= AUTO_BATCH_MIN_API) {
            setOf(
                CALLBACK_TYPE_ALL_MATCHES,
                CALLBACK_TYPE_FIRST_MATCH,
                CALLBACK_TYPE_MATCH_LOST,
                CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
                CALLBACK_TYPE_FIRST_MATCH_AND_MATCH_LOST,
            )
        } else {
            setOf(
                CALLBACK_TYPE_ALL_MATCHES,
                CALLBACK_TYPE_FIRST_MATCH,
                CALLBACK_TYPE_MATCH_LOST,
                CALLBACK_TYPE_FIRST_MATCH_AND_MATCH_LOST,
            )
        }
        if (callbackType !in validTypes) {
            return "invalid callback type - $callbackType"
        }
        if (callbackType == CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH &&
            reportDelayMillis < AUTO_BATCH_MIN_REPORT_DELAY_MILLIS
        ) {
            return "report delay for auto batch must be >= $AUTO_BATCH_MIN_REPORT_DELAY_MILLIS"
        }
        return null
    }
}
