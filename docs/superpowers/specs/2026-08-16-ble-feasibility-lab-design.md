# BLE Feasibility Lab — Design Specification

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a disposable Android BLE feasibility lab to measure real-world proximity detection reliability, privacy compatibility, background capability, and battery impact — answering whether BLE can support Ghost's proximity requirements.

**Architecture:** Modular Kotlin/Jetpack Compose Android app with isolated experiment modules (BLE advertising, scanning, background service, ephemeral ID generator, measurement recorder, battery monitor, export, UI). Each module has a single responsibility and communicates through well-defined interfaces. The lab is completely isolated from the Ghost product codebase.

**Tech Stack:**
- Kotlin + Jetpack Compose (Material3)
- Android SDK 34+ (target), minSdk 31 (Android 12+)
- Gradle with Kotlin DSL
- Room for local experiment storage
- Foreground Service with `connectedDevice` type for background BLE
- Bluetooth LE APIs (BluetoothLeScanner, BluetoothLeAdvertiser)
- CSV/JSON export via FileProvider

## Global Constraints

- **Isolation:** Lab must NOT modify src/ of existing Ghost web prototype, NOT modify Phase 1 UI, NOT create Ghost production architecture, NOT implement real encounters/reveal/connection/accounts/backend/PSI
- **Privacy:** NO ACCESS_FINE_LOCATION, NO ACCESS_COARSE_LOCATION, NO background location, NO GPS access. Use only BLUETOOTH_SCAN, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT with neverForLocation flag
- **Target:** Android 14+ (API 34), minSdk 31
- **OEM Testing:** Design for Pixel, Samsung, Xiaomi, OnePlus — no OEM-specific hacks in first implementation
- **Measurement Focus:** All data must be experiment-local (timestamp, rotating test ID, RSSI, device-local experiment ID, test condition, optional manual distance label)
- **No Network:** No cloud upload, no network sync, no external analytics
- **Deterministic Logic:** Separate pure logic (ephemeral ID generation, rotation timing, detection aggregation, latency calculation) from hardware-dependent BLE code — unit test the pure logic
- **Documentation:** Must explain purpose, architecture, Android requirements, permissions, build/install/run procedures, test matrix, export, known limitations, experimental weaknesses

---

## 1. Architecture Overview

### Module Boundaries

```
ble-feasibility-lab/
├── app/                          # Compose UI, navigation, DI
├── ble-advertiser/               # BLE advertising with rotating IDs
├── ble-scanner/                  # BLE scanning with detection recording
├── background-service/           # Foreground service for background operation
├── ephemeral-id/                 # Pure logic: rotating identifier generation
├── experiment-state/             # Experiment configuration & state machine
├── measurement-recorder/         # Detection record storage & aggregation
├── battery-monitor/              # Battery measurement & consumption tracking
├── export/                       # CSV/JSON export via FileProvider
└── test/                         # Unit tests for pure logic modules
```

### Data Flow

```
Advertiser Mode:
┌─────────────┐    ┌──────────────┐    ┌──────────────────┐    ┌──────────┐
│ EphemeralID │───▶│ BLEAdvertiser│───▶│ ForegroundSvc    │───▶│ Bluetooth│
│ Generator   │    │ (rotate IDs) │    │ (start/stop adv) │    │ Radio    │
└─────────────┘    └──────────────┘    └──────────────────┘    └──────────┘

Scanner Mode:
┌──────────┐    ┌──────────────┐    ┌─────────────────┐    ┌────────────────────┐
│ Bluetooth│───▶│ BLEScanner   │───▶│ Measurement     │───▶│ Experiment Storage │
│ Radio    │    │ (PendingInt) │    │ Recorder        │    │ (Room)             │
└──────────┘    └──────────────┘    └─────────────────┘    └────────────────────┘
                                                    │
                                                    ▼
                                            ┌─────────────────┐
                                            │ Battery Monitor │
                                            └─────────────────┘
                                                    │
                                                    ▼
                                            ┌─────────────────┐
                                            │ Export (CSV/JSON)│
                                            └─────────────────┘
```

---

## 2. Module Specifications

### 2.1 Ephemeral ID Generator (Pure Logic — Unit Testable)

**File:** `ephemeral-id/src/main/kotlin/com/ghost/blelab/ephemeral/EphemeralIdGenerator.kt`

**Interface:**
```kotlin
interface EphemeralIdGenerator {
    fun generateDailyKey(): ByteArray  // 16 bytes, cryptographically random
    fun deriveEphemeralId(dailyKey: ByteArray, timeSlot: Long): ByteArray  // 16 bytes
    fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long
    fun getRotationIntervalMinutes(): Int
    fun setRotationIntervalMinutes(minutes: Int)
}
```

**Requirements:**
- 10-minute default rotation interval (configurable)
- Identifier changes periodically based on time slots
- No device name, hardware IDs, GPS, username, account identity
- Cryptographically generated (SecureRandom + HKDF-SHA256)
- GAEN-style: Daily Tracing Key (16 bytes) → Rolling Proximity Identifier (16 bytes) per 10-min window
- Time slot = floor(currentTimeMillis / (rotationIntervalMinutes * 60 * 1000))

**Unit Tests:**
- `testRotationIntervalDefault10Minutes`
- `testRotationIntervalConfigurable`
- `testSameTimeSlotProducesSameId`
- `testDifferentTimeSlotProducesDifferentId`
- `testDifferentDailyKeyProducesDifferentId`
- `testIdDoesNotContainDeviceInfo`
- `testIdLength16Bytes`

---

### 2.2 BLE Advertiser

**File:** `ble-advertiser/src/main/kotlin/com/ghost/blelab/advertiser/BleAdvertiser.kt`

**Interface:**
```kotlin
interface BleAdvertiser {
    fun startAdvertising(ephemeralId: ByteArray, txPowerLevel: Int): Result<Unit>
    fun stopAdvertising(): Result<Unit>
    fun setAdvertisingCallback(callback: AdvertisingCallback)
    fun isAdvertising(): Boolean
}

sealed interface AdvertisingCallback {
    data class OnStartSuccess(val settings: AdvertiseSettings) : AdvertisingCallback
    data class OnStartFailure(val errorCode: Int) : AdvertisingCallback
}
```

**Requirements:**
- Uses `BluetoothLeAdvertiser` with `ADVERTISE_MODE_LOW_POWER` (default ~100ms interval)
- Advertises rotating ephemeral ID in manufacturer data or service data
- Includes experiment service UUID for filtering
- TX power configurable (default: ADVERTISE_TX_POWER_MEDIUM)
- Runs from foreground service
- Handles Android 12+ permission model (BLUETOOTH_ADVERTISE with neverForLocation)
- Graceful shutdown on stop

---

### 2.3 BLE Scanner

**File:** `ble-scanner/src/main/kotlin/com/ghost/blelab/scanner/BleScanner.kt`

**Interface:**
```kotlin
interface BleScanner {
    fun startScanning(filters: List<ScanFilter>, settings: ScanSettings, pendingIntent: PendingIntent): Result<Unit>
    fun stopScanning(pendingIntent: PendingIntent): Result<Unit>
    fun setScanCallback(callback: ScanCallback)
}

sealed interface ScanCallback {
    data class OnScanResult(val result: ScanResult) : ScanCallback
    data class OnBatchScanResults(val results: List<ScanResult>) : ScanCallback
    data class OnScanFailed(val errorCode: Int) : ScanCallback
}
```

**Requirements:**
- Uses `BluetoothLeScanner.startScan()` with `PendingIntent` for background delivery
- `SCAN_MODE_LOW_POWER` duty cycle (~10%)
- `CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH` for screen-off batching
- Filters by experiment service UUID
- Extracts ephemeral ID from advertisement data
- Records RSSI, timestamp, scan result
- Handles Android 12+ permission model (BLUETOOTH_SCAN with neverForLocation)
- Works when screen off, locked, app backgrounded

---

### 2.4 Background Service

**File:** `background-service/src/main/kotlin/com/ghost/blelab/service/BleExperimentService.kt`

**Interface:**
```kotlin
class BleExperimentService : Service() {
    companion object {
        const val ACTION_START_ADVERTISING = "START_ADVERTISING"
        const val ACTION_STOP_ADVERTISING = "STOP_ADVERTISING"
        const val ACTION_START_SCANNING = "START_SCANNING"
        const val ACTION_STOP_SCANNING = "STOP_SCANNING"
        const val EXTRA_EPHEMERAL_ID = "EPHEMERAL_ID"
        const val EXTRA_ROTATION_INTERVAL = "ROTATION_INTERVAL"
    }
}
```

**Requirements:**
- Foreground service with type `connectedDevice` (Android 14+)
- Persistent notification: "BLE Feasibility Experiment Running — Tap to stop"
- Notification clearly states this is a BLE feasibility experiment
- Explicit start/stop via Intent actions
- Manages advertiser/scanner lifecycle
- Survives screen-off, lock, app removed from recents
- `START_STICKY` for system restart
- Logs state changes for post-test analysis
- Graceful shutdown on stop

---

### 2.5 Experiment State

**File:** `experiment-state/src/main/kotlin/com/ghost/blelab/experiment/ExperimentController.kt`

**Interface:**
```kotlin
data class ExperimentConfig(
    val role: Role,  // ADVERTISER or SCANNER
    val rotationIntervalMinutes: Int = 10,
    val scanMode: ScanMode = ScanMode.LOW_POWER,
    val advertisingMode: AdvertisingMode = AdvertisingMode.LOW_POWER,
    val txPowerLevel: Int = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM,
    val testCondition: TestCondition = TestCondition.UNSPECIFIED,
)

enum class Role { ADVERTISER, SCANNER }
enum class ScanMode { LOW_POWER, BALANCED, LOW_LATENCY }
enum class AdvertisingMode { LOW_POWER, BALANCED, LOW_LATENCY }

data class TestCondition(
    val distanceMeters: Int? = null,  // 1, 3, 5, 10, 20
    val environment: Environment = Environment.UNSPECIFIED,
    val deviceState: DeviceState = DeviceState.UNSPECIFIED,
    val orientation: Orientation = Orientation.UNSPECIFIED,
    val pocketState: PocketState = PocketState.UNSPECIFIED,
)

enum class Environment { UNSPECIFIED, OPEN_INDOOR, CROWDED_INDOOR, APARTMENT_WALL, DIFFERENT_ROOM, DIFFERENT_FLOOR, OUTDOOR_OPEN }
enum class DeviceState { UNSPECIFIED, SCREEN_ON, SCREEN_OFF, LOCKED, BACKGROUNDED, REMOVED_FROM_RECENTS }
enum class Orientation { UNSPECIFIED, FACING_EACH_OTHER, BACK_TO_BACK, ONE_IN_POCKET, BOTH_IN_POCKET }
enum class PocketState { UNSPECIFIED, NOT_IN_POCKET, IN_POCKET }

interface ExperimentController {
    fun startExperiment(config: ExperimentConfig): Result<Unit>
    fun stopExperiment(): Result<Unit>
    fun getCurrentConfig(): ExperimentConfig?
    fun isRunning(): Boolean
    fun setTestCondition(condition: TestCondition)
}
```

**Requirements:**
- Manages experiment lifecycle
- Persists config to Room for survival across process death
- Exposes current state to UI
- Allows manual test condition selection
- No network, no cloud sync

---

### 2.6 Measurement Recorder

**File:** `measurement-recorder/src/main/kotlin/com/ghost/blelab/measurement/MeasurementRecorder.kt`

**Interface:**
```kotlin
@Entity(tableName = "detection_records")
data class DetectionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localTimestamp: Long,  // System.currentTimeMillis()
    val ephemeralId: ByteArray,  // 16 bytes
    val rssi: Int,  // dBm
    val scanResultTimestamp: Long,  // ScanResult.timestampNanos
    val deviceLocalExperimentId: String,  // UUID for this experiment run
    val testCondition: String,  // JSON of TestCondition
    val distanceLabelMeters: Int? = null,  // Manual entry
)

data class AggregatedStats(
    val totalScans: Long,
    val detections: Long,
    val detectionRate: Double,
    val averageRssi: Double,
    val rssiVariance: Double,
    val minRssi: Int,
    val maxRssi: Int,
    val latencyStats: LatencyStats,
)

data class LatencyStats(
    val minLatencyMs: Long,
    val maxLatencyMs: Long,
    val avgLatencyMs: Double,
    val medianLatencyMs: Long,
)

interface MeasurementRecorder {
    fun recordDetection(record: DetectionRecord): Result<Unit>
    fun getAggregatedStats(experimentId: String): Result<AggregatedStats>
    fun getAllRecords(experimentId: String): Result<List<DetectionRecord>>
    fun clearExperiment(experimentId: String): Result<Unit>
}
```

**Requirements:**
- Room database for local storage
- Records only experiment-local information
- NO GPS coordinates, Wi-Fi SSIDs, Bluetooth MAC addresses, phone contacts, account identifiers, analytics identifiers
- Supports aggregation queries for UI display
- Export-ready schema

---

### 2.7 Battery Monitor

**File:** `battery-monitor/src/main/kotlin/com/ghost/blelab/battery/BatteryMonitor.kt`

**Interface:**
```kotlin
data class BatterySnapshot(
    val timestamp: Long,
    val batteryLevel: Int,  // 0-100
    val isCharging: Boolean,
    val chargingType: Int,  // BatteryManager.BATTERY_PLUGGED_*
    val temperature: Float,
    val voltage: Int,
    val health: Int,
)

data class BatteryExperimentResult(
    val initialBatteryPercent: Int,
    val finalBatteryPercent: Int,
    val durationMinutes: Long,
    val scanConfig: String,  // JSON of scan/advertising config
    val screenState: String,  // JSON of screen state timeline
    val deviceModel: String,
    val androidVersion: String,
    val approximateConsumptionRatePercentPerHour: Double,
)

interface BatteryMonitor {
    fun startMonitoring(experimentId: String): Result<Unit>
    fun stopMonitoring(): Result<BatteryExperimentResult>
    fun getCurrentSnapshot(): BatterySnapshot
    fun recordScreenStateChange(state: DeviceState)
}
```

**Requirements:**
- Records initial battery % at experiment start
- Records final battery % at experiment end
- Tracks duration, scan config, advertising config, screen state
- Uses `BatteryManager` API
- Does NOT claim precise battery drain from API alone
- Controlled repeated tests are primary evidence
- Exports device model, Android version for OEM comparison

---

### 2.8 Export

**File:** `export/src/main/kotlin/com/ghost/blelab/export/ExperimentExporter.kt`

**Interface:**
```kotlin
enum class ExportFormat { CSV, JSON, PLAIN_TEXT }

data class ExportResult(
    val fileUri: Uri,
    val format: ExportFormat,
    val recordCount: Int,
    val fileSizeBytes: Long,
)

interface ExperimentExporter {
    fun exportExperiment(
        experimentId: String,
        format: ExportFormat,
        context: Context
    ): Result<ExportResult>
    fun exportAllExperiments(
        format: ExportFormat,
        context: Context
    ): Result<ExportResult>
}
```

**Requirements:**
- CSV: One row per detection record, headers for all fields
- JSON: Array of detection records with metadata
- Plain text: Human-readable summary + records
- Uses `FileProvider` for sharing (no external storage permission)
- No cloud upload, no network sync, no external analytics
- Contains enough information to reproduce analysis

---

### 2.9 UI (Compose)

**Screens:**

1. **Device Role Screen** — Radio buttons: Advertiser / Scanner
2. **Experiment Control Screen** — Start/Stop, rotation interval, scan mode, advertising mode, TX power
3. **Live Telemetry Screen** — Current rotating test ID, advertising/scanning status, detections count, latest RSSI, latest timestamp, elapsed time
4. **Test Condition Screen** — Distance (1/3/5/10/20m), Environment, Device State, Orientation, Pocket State
5. **Results Screen** — Total scans, detections, detection rate, avg RSSI, RSSI variance, min/max RSSI, latency stats
6. **Battery Screen** — Starting %, current %, elapsed time, consumption rate

**Design:** Utilitarian, not Ghost-branded. Material3 with dense information display. No animations, no emotional design.

---

## 3. Permissions

**AndroidManifest.xml permissions:**
```xml
<!-- Android 12+ (API 31+) -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT"
    android:usesPermissionFlags="neverForLocation" />

<!-- Foreground service -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Battery stats (optional, for detailed monitoring) -->
<uses-permission android:name="android.permission.BATTERY_STATS" />
```

**No location permissions requested.**

---

## 4. Test Matrix Implementation

The UI exposes all test matrix dimensions as manual selectors:

| Dimension | Values |
|-----------|--------|
| Distances | 1m, 3m, 5m, 10m, 20m |
| Environments | Open indoor, Crowded indoor, Apartment/wall, Different room, Different floor, Outdoor open, Pocket |
| Device States | Screen on, Screen off, Locked, Backgrounded, Removed from recents |
| Orientations | Facing, Back-to-back, One in pocket, Both in pocket |

Each detection record includes the selected test condition at time of detection.

---

## 5. Automated Tests (Pure Logic Only)

**Module:** `test/`

**Test Classes:**
- `EphemeralIdGeneratorTest.kt` — All generator logic
- `ExperimentAggregationTest.kt` — Detection rate, RSSI aggregation, latency calculation
- `TestConditionSerializationTest.kt` — JSON round-trip
- `BatteryConsumptionCalculationTest.kt` — Rate calculation

**No Android radio behavior unit tests** — hardware-dependent code is integration-tested only.

---

## 6. Documentation

**File:** `docs/BLE_FEASIBILITY_LAB.md`

**Sections:**
1. Purpose
2. Architecture
3. Android Requirements
4. Permissions (with justification)
5. How to Build (Gradle commands)
6. How to Install (adb install)
7. Advertiser Procedure
8. Scanner Procedure
9. Distance Tests
10. Background Tests
11. Battery Tests
12. Export Procedure
13. Known Android Limitations
14. Known Experimental Weaknesses
15. Assumptions (clearly labeled)

---

## 7. Success Criteria (from Mission)

1. ✅ Two physical Android devices can advertise and detect one another
2. ✅ Rotating identifiers work
3. ✅ No location permission requested
4. ✅ Detection records contain no GPS or hardware identity
5. ✅ Foreground detection works
6. ✅ Background/locked testing can be performed
7. ✅ Battery experiments can be run
8. ✅ Results can be exported
9. ✅ Deterministic logic has tests
10. ✅ Lab remains isolated from Ghost product code

---

## 8. What NOT to Conclude

Even if experiment works:
- ❌ "Ghost proximity is solved"
- ✅ "The current BLE experiment demonstrated X under conditions Y"

---

## 9. Implementation Report (Final Deliverable)

After implementation, produce: `docs/BLE_FEASIBILITY_LAB_IMPLEMENTATION_REPORT.md`

Sections:
1. What Was Built
2. Files / Modules Added
3. Android Versions Tested
4. Permissions Used
5. How the Experiment Works
6. How to Run Advertiser
7. How to Run Scanner
8. Measurement Method
9. Battery Measurement Method
10. Known Limitations
11. What Has NOT Been Proven
12. Recommended Physical Test Procedure

Each section labeled: FACT / IMPLEMENTED / UNTESTED / UNKNOWN