package com.ghost.blelab.ble.scanner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ScanConfigResolver] — pure JVM, no Android hardware.
 *
 * Encodes the two physical-device failures as regression tests:
 *  - Galaxy A03 (API 33): "invalid callback type-8"
 *  - Galaxy Tab A9+ (API 36): "report delay for auto batch must be >= 600000"
 *
 * The validator mirrors the AOSP ScanSettings.Builder validation logic
 * (packages/modules/Bluetooth, tags android-13.0.0_r1 / android-14.0.0_r1 /
 * android-16.0.0_r1) so we can prove the resolved config would be accepted
 * by the framework on both devices.
 */
class ScanConfigResolverTest {

    // --- resolve(): unified config valid on both test devices ---

    @Test
    fun `resolve on API 33 returns ALL_MATCHES with zero report delay`() {
        val config = ScanConfigResolver.resolve(33)
        assertEquals(ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES, config.callbackType)
        assertEquals(0L, config.reportDelayMillis)
        assertFalse(config.usesAutoBatch)
    }

    @Test
    fun `resolve on API 36 returns ALL_MATCHES with zero report delay`() {
        val config = ScanConfigResolver.resolve(36)
        assertEquals(ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES, config.callbackType)
        assertEquals(0L, config.reportDelayMillis)
        assertFalse(config.usesAutoBatch)
    }

    @Test
    fun `resolve is identical on API 33 and API 36 for cross-device comparability`() {
        val api33 = ScanConfigResolver.resolve(33)
        val api36 = ScanConfigResolver.resolve(36)
        assertEquals(api33.callbackType, api36.callbackType)
        assertEquals(api33.reportDelayMillis, api36.reportDelayMillis)
        assertEquals(api33.usesAutoBatch, api36.usesAutoBatch)
    }

    @Test
    fun `resolved config passes framework validation on API 33`() {
        val config = ScanConfigResolver.resolve(33)
        assertNull(ScanConfigResolver.validate(config.callbackType, config.reportDelayMillis, 33))
    }

    @Test
    fun `resolved config passes framework validation on API 36`() {
        val config = ScanConfigResolver.resolve(36)
        assertNull(ScanConfigResolver.validate(config.callbackType, config.reportDelayMillis, 36))
    }

    @Test
    fun `resolve rejects API below 21`() {
        assertFailsWith<IllegalArgumentException> { ScanConfigResolver.resolve(20) }
    }

    // --- validate(): reproduces the two physical failures ---

    @Test
    fun `AUTO_BATCH callback type 8 is rejected on API 33 - Galaxy A03 failure`() {
        val error = ScanConfigResolver.validate(
            callbackType = ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
            reportDelayMillis = 0L,
            sdkInt = 33,
        )
        assertEquals("invalid callback type - 8", error)
    }

    @Test
    fun `AUTO_BATCH with zero report delay is rejected on API 36 - Galaxy Tab A9+ failure`() {
        val error = ScanConfigResolver.validate(
            callbackType = ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
            reportDelayMillis = 0L,
            sdkInt = 36,
        )
        assertEquals("report delay for auto batch must be >= 600000", error)
    }

    @Test
    fun `AUTO_BATCH with 600000ms report delay is accepted on API 34+`() {
        assertNull(
            ScanConfigResolver.validate(
                callbackType = ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
                reportDelayMillis = ScanConfigResolver.AUTO_BATCH_MIN_REPORT_DELAY_MILLIS,
                sdkInt = 34,
            )
        )
        assertNull(
            ScanConfigResolver.validate(
                callbackType = ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
                reportDelayMillis = ScanConfigResolver.AUTO_BATCH_MIN_REPORT_DELAY_MILLIS,
                sdkInt = 36,
            )
        )
    }

    @Test
    fun `AUTO_BATCH with report delay just below minimum is rejected on API 34+`() {
        val error = ScanConfigResolver.validate(
            callbackType = ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH,
            reportDelayMillis = ScanConfigResolver.AUTO_BATCH_MIN_REPORT_DELAY_MILLIS - 1,
            sdkInt = 36,
        )
        assertNotNull(error)
        assertTrue(error.startsWith("report delay for auto batch must be >="))
    }

    @Test
    fun `negative report delay is rejected on all API levels`() {
        assertEquals(
            "reportDelay must be > 0",
            ScanConfigResolver.validate(ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES, -1L, 33)
        )
        assertEquals(
            "reportDelay must be > 0",
            ScanConfigResolver.validate(ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES, -1L, 36)
        )
    }

    @Test
    fun `FIRST_MATCH plus MATCH_LOST combination is valid on all API levels`() {
        val combined = ScanConfigResolver.CALLBACK_TYPE_FIRST_MATCH_AND_MATCH_LOST
        assertNull(ScanConfigResolver.validate(combined, 0L, 33))
        assertNull(ScanConfigResolver.validate(combined, 0L, 36))
    }

    @Test
    fun `unknown callback types are rejected on all API levels`() {
        assertEquals("invalid callback type - 16", ScanConfigResolver.validate(16, 0L, 33))
        assertEquals("invalid callback type - 16", ScanConfigResolver.validate(16, 0L, 36))
        assertEquals("invalid callback type - 0", ScanConfigResolver.validate(0, 0L, 33))
    }

    @Test
    fun `framework constants match AOSP values`() {
        // Guard against accidental constant drift vs android.bluetooth.le.ScanSettings
        assertEquals(1, ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES)
        assertEquals(2, ScanConfigResolver.CALLBACK_TYPE_FIRST_MATCH)
        assertEquals(4, ScanConfigResolver.CALLBACK_TYPE_MATCH_LOST)
        assertEquals(8, ScanConfigResolver.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH)
        assertEquals(600_000L, ScanConfigResolver.AUTO_BATCH_MIN_REPORT_DELAY_MILLIS)
        assertEquals(34, ScanConfigResolver.AUTO_BATCH_MIN_API)
    }
}
