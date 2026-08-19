# BLE Feasibility Lab — Implementation Plan

> **RECONCILIATION NOTE (2026-08-19):** This plan's checkbox state was reconciled against the verified repository state at commit 125a998 (CI run 32213571392, 53/53 unit tests passed, APK build successful). Checkboxes now reflect build-verified implementation. Hardware-dependent steps (manual device tests, physical verification) remain UNCHECKED because no physical experiment has been executed. See the Phase Status Table below.

## Phase Status Table (as of commit 125a998, 2026-08-19)

| Phase | Scope | Status | Evidence |
|-------|-------|--------|----------|
| A | Project/build skeleton + core utilities | IMPLEMENTED + BUILD-VERIFIED | CI 32213571392 green |
| B | Ephemeral ID generator | IMPLEMENTED + BUILD-VERIFIED + UNIT-TESTED | EphemeralIdGeneratorTest |
| C | BLE advertiser | IMPLEMENTED + BUILD-VERIFIED + UNIT-TESTED (payload) | AdvertisePayloadTest; physical advertising UNVERIFIED |
| D | BLE scanner | IMPLEMENTED + BUILD-VERIFIED | Physical scanning UNVERIFIED |
| E | Detection recorder | IMPLEMENTED + BUILD-VERIFIED | Planned AggregationTest.kt NOT written |
| F | Foreground service | IMPLEMENTED + BUILD-VERIFIED + UNIT-TESTED (rotation logic) | BleExperimentService + ServiceController + RotationCoordinator at commit 714b85c (CI run 32229842724, 60/60 unit tests, APK build successful); physical foreground/notification/rotation behavior UNVERIFIED |
| G | Experiment state & config | IMPLEMENTED + BUILD-VERIFIED | Run persistence implemented; ServiceController delivered in Phase F (714b85c) |
| H | Battery monitor | NOT STARTED | No BatteryMonitor source exists |
| I | Export | IMPLEMENTED + BUILD-VERIFIED (CSV only) | JSON/PLAIN_TEXT formats removed per commit 99acdf2; physical export UNVERIFIED |
| J | Minimal experiment UI | IMPLEMENTED + BUILD-VERIFIED | TelemetryScreen (J5) and BatteryScreen (J8) NOT implemented; 2026-08-19 UI usability fix pass (e227730) + end-to-end audit fix pass (a4c2d13, CI 32244881362, 74/74 tests): dropdowns rewritten without ExposedDropdownMenuBox, lazy BLE handle resolution, observable StartStatus with actionable failure reasons, wall condition per protocol, CSV protocol columns; on-device usability PHYSICALLY UNVERIFIED |
| K | Automated tests | PARTIAL | 53 tests pass; AggregationTest and BatteryRateTest missing |
| L | Documentation | PARTIAL | Protocol + pilot report exist; final implementation report not written |
| M | Verification & finalization | PARTIAL | Build/test verification done via CI; all physical verification steps NOT done |

**Key facts:**
- BLE feasibility is UNDECIDED. No physical measurements exist.
- Phase F (foreground service) is implemented and build-verified; background experiments can now be attempted on physical devices (physical verification still pending).
- Battery impact cannot be measured until Phase H (battery monitor) is implemented.
- All "manual test on device" steps remain physically unverified.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a disposable Android BLE feasibility lab (single-module Gradle app) to measure real-world proximity detection reliability, privacy compatibility, background capability, and battery impact.

**Architecture:** Single Android application module with clean internal package boundaries. All experiment logic isolated from Ghost product codebase. No Room initially — simple file-based persistence. Foreground service with `connectedDevice` type for background BLE. GAEN-style rotating ephemeral IDs (10-min default, configurable) in service data with dedicated 128-bit UUID.

**Tech Stack:**
- Kotlin + Jetpack Compose (Material3)
- Android SDK 34 (target), minSdk 31 (Android 12+)
- Gradle with Kotlin DSL
- Bluetooth LE APIs (BluetoothLeScanner, BluetoothLeAdvertiser)
- Foreground Service with `connectedDevice` type
- File-based persistence (JSON/CSV) + FileProvider for export
- SQLite/Room only if measurement volume justifies

## Global Constraints

- **Isolation:** Lab in `ble-feasibility-lab/` directory. Must NOT modify Ghost web prototype (`src/`, `dist/`, `package.json`, etc.). Must NOT modify Constitution, Roadmap, Product Spec, or Decisions.
- **Privacy:** NO ACCESS_FINE_LOCATION, NO ACCESS_COARSE_LOCATION, NO background location, NO GPS. Only BLUETOOTH_SCAN, BLUETOOTH_ADVERTISE, BLUETOOTH_CONNECT with `neverForLocation`.
- **Target:** Android 14+ (API 34), minSdk 31. Test on real physical devices (Pixel, Samsung, Xiaomi, OnePlus).
- **No Network:** No cloud upload, no network sync, no external analytics.
- **Deterministic Logic:** Pure logic (ephemeral ID, rotation, aggregation, latency, battery rate) separated from hardware-dependent BLE code — unit test pure logic.
- **Experimental Velocity:** Get minimal end-to-end advertiser/scanner working first. Polish UI later.
- **Documentation:** All assumptions labeled. Final implementation report with FACT/IMPLEMENTED/UNTESTED/UNKNOWN.

---

## File Structure

```
ble-feasibility-lab/
├── build.gradle.kts                 # Module build config
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml      # Permissions, service, provider
│   │   ├── java/com/ghost/blelab/
│   │   │   ├── BleLabApplication.kt
│   │   │   ├── ephemeral/           # Pure logic — unit testable
│   │   │   │   ├── EphemeralIdGenerator.kt
│   │   │   │   ├── EphemeralIdGeneratorImpl.kt
│   │   │   │   └── TimeSlotCalculator.kt
│   │   │   ├── ble/
│   │   │   │   ├── advertiser/
│   │   │   │   │   ├── BleAdvertiser.kt
│   │   │   │   │   ├── BleAdvertiserImpl.kt
│   │   │   │   │   └── AdvertisePayload.kt
│   │   │   │   ├── scanner/
│   │   │   │   │   ├── BleScanner.kt
│   │   │   │   │   ├── BleScannerImpl.kt
│   │   │   │   │   └── ScanResultProcessor.kt
│   │   │   │   └── common/
│   │   │   │       ├── ExperimentUuid.kt
│   │   │   │       └── BleConstants.kt
│   │   │   ├── service/
│   │   │   │   ├── BleExperimentService.kt
│   │   │   │   └── ServiceController.kt
│   │   │   ├── experiment/
│   │   │   │   ├── ExperimentController.kt
│   │   │   │   ├── ExperimentControllerImpl.kt
│   │   │   │   ├── ExperimentConfig.kt
│   │   │   │   ├── TestCondition.kt
│   │   │   │   └── ExperimentRun.kt
│   │   │   ├── measurement/
│   │   │   │   ├── DetectionRecord.kt
│   │   │   │   ├── MeasurementRecorder.kt
│   │   │   │   ├── MeasurementRecorderImpl.kt
│   │   │   │   └── AggregatedStats.kt
│   │   │   ├── battery/
│   │   │   │   ├── BatteryMonitor.kt
│   │   │   │   ├── BatteryMonitorImpl.kt
│   │   │   │   └── BatteryExperimentResult.kt
│   │   │   ├── export/
│   │   │   │   ├── ExperimentExporter.kt
│   │   │   │   └── ExperimentExporterImpl.kt
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── navigation/
│   │   │   │   ├── screens/
│   │   │   │   │   ├── RoleScreen.kt
│   │   │   │   │   ├── ControlScreen.kt
│   │   │   │   │   ├── TelemetryScreen.kt
│   │   │   │   │   ├── TestConditionScreen.kt
│   │   │   │   │   ├── ResultsScreen.kt
│   │   │   │   │   └── BatteryScreen.kt
│   │   │   │   ├── components/
│   │   │   │   │   └── ...
│   │   │   │   └── theme/
│   │   │   │       └── BleLabTheme.kt
│   │   │   └── util/
│   │   │       ├── FileUtil.kt
│   │   │       ├── JsonUtil.kt
│   │   │       └── TimeUtil.kt
│   │   └── res/
│   │       ├── values/strings.xml
│   │       ├── xml/file_paths.xml
│   │       └── drawable/
│   └── test/
│       └── java/com/ghost/blelab/
│           ├── ephemeral/
│           │   └── EphemeralIdGeneratorTest.kt
│           ├── experiment/
│           │   └── AggregationTest.kt
│           ├── measurement/
│           │   └── LatencyCalculationTest.kt
│           ├── battery/
│           │   └── BatteryRateTest.kt
│           └── util/
│               └── JsonSerializationTest.kt
├── docs/
│   ├── BLE_FEASIBILITY_LAB.md
│   └── BLE_FEASIBILITY_LAB_IMPLEMENTATION_REPORT.md
└── settings.gradle.kts              # Project settings (single module)
```

---

## Task Breakdown

### Phase A: Project/Build Skeleton

#### Task A1: Create Gradle Project Structure

**Files:**
- Create: `ble-feasibility-lab/settings.gradle.kts`
- Create: `ble-feasibility-lab/build.gradle.kts`
- Create: `ble-feasibility-lab/src/main/AndroidManifest.xml`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/BleLabApplication.kt`
- Create: `ble-feasibility-lab/src/main/res/values/strings.xml`
- Create: `ble-feasibility-lab/src/main/res/xml/file_paths.xml`

**Interfaces:**
- Produces: Buildable Android project with correct permissions and manifest

```kotlin
// settings.gradle.kts
rootProject.name = "ble-feasibility-lab"
```

```kotlin
// build.gradle.kts
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
```

```xml
<!-- AndroidManifest.xml key permissions -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" android:usesPermissionFlags="neverForLocation"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" android:usesPermissionFlags="neverForLocation"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" android:usesPermissionFlags="neverForLocation"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.BATTERY_STATS"/>

<application ...>
    <service android:name=".service.BleExperimentService"
        android:foregroundServiceType="connectedDevice"
        android:exported="false"/>
    <provider android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
    </provider>
</application>
```

```xml
<!-- file_paths.xml -->
<paths>
    <files-path name="exports" path="exports/"/>
</paths>
```

**Steps:**
- [x] Step 1: Create `settings.gradle.kts`
- [x] Step 2: Create `build.gradle.kts` with Compose, Kotlin, Android plugin
- [x] Step 3: Create `AndroidManifest.xml` with all permissions
- [x] Step 4: Create `BleLabApplication.kt` (minimal Application subclass)
- [x] Step 5: Create `strings.xml` with app name
- [x] Step 6: Create `file_paths.xml` for FileProvider
- [x] Step 7: Verify build: `./gradlew :ble-feasibility-lab:assembleDebug`

---

#### Task A2: Core Utilities

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/util/FileUtil.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/util/JsonUtil.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/util/TimeUtil.kt`

**Interfaces:**
- Produces: `FileUtil.writeJson()`, `FileUtil.readJson()`, `FileUtil.appendCsv()`, `JsonUtil.toJson()`, `JsonUtil.fromJson()`, `TimeUtil.nowMillis()`, `TimeUtil.formatDuration()`

**Steps:**
- [x] Step 1: Write `FileUtil` with atomic write, CSV append, directory management
- [x] Step 2: Write `JsonUtil` using Kotlinx serialization
- [x] Step 3: Write `TimeUtil` for timestamps and formatting
- [x] Step 4: Add `kotlinx-serialization-json` to dependencies
- [x] Step 5: Build verify

---

### Phase B: Ephemeral ID Generator (Pure Logic — TDD)

#### Task B1: Ephemeral ID Interfaces & Types

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ephemeral/TimeSlotCalculator.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ephemeral/EphemeralIdGenerator.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ephemeral/EphemeralIdGeneratorImpl.kt`

**Interfaces:**
```kotlin
// TimeSlotCalculator.kt
interface TimeSlotCalculator {
    fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long
    fun getTimeSlotStartMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long
    fun getTimeSlotEndMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long
}

// EphemeralIdGenerator.kt
interface EphemeralIdGenerator {
    fun generateDailyKey(): ByteArray
    fun deriveEphemeralId(dailyKey: ByteArray, timeSlot: Long): ByteArray
    fun getCurrentEphemeralId(dailyKey: ByteArray, rotationIntervalMinutes: Int): ByteArray
    fun getRotationIntervalMinutes(): Int
    fun setRotationIntervalMinutes(minutes: Int)
}

// EphemeralIdGeneratorImpl.kt
class EphemeralIdGeneratorImpl : EphemeralIdGenerator { ... }
```

**Steps:**
- [x] Step 1: Write failing test `EphemeralIdGeneratorTest.testRotationIntervalDefault10Minutes`
- [x] Step 2: Run test → verify RED
- [x] Step 3: Implement `TimeSlotCalculator` (pure math)
- [x] Step 4: Implement `EphemeralIdGeneratorImpl` using HKDF-SHA256
- [x] Step 5: Run test → verify GREEN
- [x] Step 6: Write remaining tests (same time slot = same ID, different slot = different ID, different key = different ID, ID length 16 bytes, no device info)
- [x] Step 7: Run all tests → verify GREEN
- [x] Step 8: Refactor if needed
- [x] Step 9: Commit

**Test Commands:**
```bash
./gradlew :ble-feasibility-lab:test --tests "com.ghost.blelab.ephemeral.EphemeralIdGeneratorTest"
```

---

### Phase C: BLE Advertiser

#### Task C1: BLE Constants & Payload

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/common/BleConstants.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/common/ExperimentUuid.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/advertiser/AdvertisePayload.kt`

**Interfaces:**
```kotlin
// BleConstants.kt
object BleConstants {
    val EXPERIMENT_SERVICE_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000") // TODO: generate real UUID
    const val PROTOCOL_VERSION: Byte = 1
    const val EPHEMERAL_ID_LENGTH = 16
}

// AdvertisePayload.kt
data class AdvertisePayload(
    val protocolVersion: Byte = BleConstants.PROTOCOL_VERSION,
    val ephemeralId: ByteArray,
) {
    fun toServiceData(): ByteArray { ... }
    companion object {
        fun fromServiceData(data: ByteArray): AdvertisePayload? { ... }
    }
}
```

**Steps:**
- [x] Step 1: Generate a real 128-bit UUID for the experiment (use `uuidgen` or similar)
- [x] Step 2: Write `BleConstants.kt` with the UUID
- [x] Step 3: Write `AdvertisePayload.kt` with serialization/deserialization
- [x] Step 4: Build verify

---

#### Task C2: BleAdvertiser Interface & Implementation

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/advertiser/BleAdvertiser.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/advertiser/BleAdvertiserImpl.kt`

**Interfaces:**
```kotlin
// BleAdvertiser.kt
interface BleAdvertiser {
    fun startAdvertising(ephemeralId: ByteArray, txPowerLevel: Int): Result<Unit>
    fun stopAdvertising(): Result<Unit>
    fun setCallback(callback: AdvertisingCallback)
    fun isAdvertising(): Boolean
}

sealed interface AdvertisingCallback {
    data class OnStartSuccess(val settings: AdvertiseSettings) : AdvertisingCallback
    data class OnStartFailure(val errorCode: Int) : AdvertisingCallback
}

// BleAdvertiserImpl.kt
class BleAdvertiserImpl(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
) : BleAdvertiser { ... }
```

**Requirements:**
- Uses `BluetoothLeAdvertiser`
- `ADVERTISE_MODE_LOW_POWER` (~100ms interval)
- Service data with experiment UUID + `AdvertisePayload`
- TX power configurable (default MEDIUM)
- Handles Android 12+ permission checks
- Rotates advertisement when ephemeral ID changes (called by service)

**Steps:**
- [x] Step 1: Write `BleAdvertiser` interface and `AdvertisingCallback`
- [x] Step 2: Implement `BleAdvertiserImpl` with advertiser lifecycle
- [x] Step 3: Add rotation support: `updateEphemeralId(newId: ByteArray)`
- [x] Step 4: Build verify
- [ ] Step 5: Manual test on device (install, start advertising, verify with nRF Connect)

---

### Phase D: BLE Scanner

#### Task D1: BleScanner Interface & Implementation

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/scanner/BleScanner.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/scanner/BleScannerImpl.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ble/scanner/ScanResultProcessor.kt`

**Interfaces:**
```kotlin
// BleScanner.kt
interface BleScanner {
    fun startScanning(pendingIntent: PendingIntent): Result<Unit>
    fun stopScanning(pendingIntent: PendingIntent): Result<Unit>
    fun setCallback(callback: ScanCallback)
}

sealed interface ScanCallback {
    data class OnScanResult(val result: ScanResult) : ScanCallback
    data class OnBatchScanResults(val results: List<ScanResult>) : ScanCallback
    data class OnScanFailed(val errorCode: Int) : ScanCallback
}

// ScanResultProcessor.kt
class ScanResultProcessor {
    fun processScanResult(scanResult: ScanResult): DetectionRecord? { ... }
}
```

**Requirements:**
- `BluetoothLeScanner.startScan()` with `PendingIntent`
- `SCAN_MODE_LOW_POWER`, `CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH`
- `ScanFilter` matching experiment service UUID
- Extracts ephemeral ID from service data using `AdvertisePayload.fromServiceData()`
- Returns `DetectionRecord` with timestamp, RSSI, ephemeral ID, scan result timestamp
- Handles Android 12+ permission checks

**Steps:**
- [x] Step 1: Write `BleScanner` interface and `ScanCallback`
- [x] Step 2: Implement `BleScannerImpl` with PendingIntent-based scanning
- [x] Step 3: Implement `ScanResultProcessor` to parse service data
- [x] Step 4: Build verify
- [ ] Step 5: Manual test on device (advertiser on device A, scanner on device B)

---

### Phase E: Detection Recorder (File-based)

#### Task E1: Measurement Types & Recorder

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/measurement/DetectionRecord.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/measurement/AggregatedStats.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/measurement/MeasurementRecorder.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/measurement/MeasurementRecorderImpl.kt`

**Interfaces:**
```kotlin
// DetectionRecord.kt
@Serializable
data class DetectionRecord(
    val localTimestamp: Long,
    val ephemeralId: ByteArray,
    val rssi: Int,
    val scanResultTimestamp: Long,
    val deviceLocalExperimentId: String,
    val testCondition: TestCondition,
    val distanceLabelMeters: Int? = null,
)

// AggregatedStats.kt
@Serializable
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

@Serializable
data class LatencyStats(
    val minLatencyMs: Long,
    val maxLatencyMs: Long,
    val avgLatencyMs: Double,
    val medianLatencyMs: Long,
)

// MeasurementRecorder.kt
interface MeasurementRecorder {
    fun recordDetection(record: DetectionRecord): Result<Unit>
    fun getAggregatedStats(experimentId: String): Result<AggregatedStats>
    fun getAllRecords(experimentId: String): Result<List<DetectionRecord>>
    fun clearExperiment(experimentId: String): Result<Unit>
}
```

**Requirements:**
- File-based: each experiment run = one JSON file (`experiments/{experimentId}.json`)
- Append-only for detection records
- Aggregation computed on demand (no Room)
- `deviceLocalExperimentId` = UUID generated at experiment start
- `testCondition` = current test condition at time of detection

**Steps:**
- [x] Step 1: Write `DetectionRecord`, `AggregatedStats`, `LatencyStats` data classes
- [x] Step 2: Write `MeasurementRecorder` interface
- [x] Step 3: Implement `MeasurementRecorderImpl` with file I/O
- [ ] Step 4: Write unit tests for aggregation logic (`AggregationTest.kt`)
- [ ] Step 5: Run tests → verify GREEN
- [x] Step 6: Build verify

---

### Phase F: Foreground Service

#### Task F1: BleExperimentService

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/service/BleExperimentService.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/service/ServiceController.kt`

**Interfaces:**
```kotlin
// BleExperimentService.kt
class BleExperimentService : Service() {
    companion object {
        const val ACTION_START_ADVERTISING = "START_ADVERTISING"
        const val ACTION_STOP_ADVERTISING = "STOP_ADVERTISING"
        const val ACTION_START_SCANNING = "START_SCANNING"
        const val ACTION_STOP_SCANNING = "STOP_SCANNING"
        const val ACTION_ROTATE_ID = "ROTATE_ID"
        const val EXTRA_CONFIG = "EXPERIMENT_CONFIG"
    }
    // Manages advertiser/scanner lifecycle, rotation timer
}

// ServiceController.kt
class ServiceController(private val context: Context) {
    fun startExperiment(config: ExperimentConfig): Result<Unit>
    fun stopExperiment(): Result<Unit>
    fun isRunning(): Boolean
    fun updateTestCondition(condition: TestCondition)
}
```

**Requirements:**
- Foreground service type `connectedDevice`
- Notification: "Ghost BLE Feasibility Lab is running" / "Tap to stop"
- Manages `BleAdvertiser` and `BleScanner` instances
- Handles rotation timer (10-min default, configurable)
- Persists experiment config for survival across process death
- `START_STICKY` for system restart
- Graceful shutdown

**Steps:**
- [x] Step 1: Write `BleExperimentService` with foreground service boilerplate
- [x] Step 2: Implement advertiser/scanner lifecycle management
- [x] Step 3: Implement rotation timer using `Handler`/`Coroutines` (coroutines + RotationCoordinator driven by existing TimeSlotCalculator)
- [x] Step 4: Write `ServiceController` for UI communication
- [x] Step 5: Build verify (CI run 32229842724, commit 714b85c)
- [ ] Step 6: Manual test: start service, verify notification, verify survival after screen off/lock — PHYSICALLY UNVERIFIED

---

### Phase G: Experiment State & Config

#### Task G1: Experiment Types & Controller

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/experiment/ExperimentConfig.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/experiment/TestCondition.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/experiment/ExperimentRun.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/experiment/ExperimentController.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/experiment/ExperimentControllerImpl.kt`

**Interfaces:**
```kotlin
// ExperimentConfig.kt
@Serializable
data class ExperimentConfig(
    val role: Role,
    val rotationIntervalMinutes: Int = 10,
    val scanMode: ScanMode = ScanMode.LOW_POWER,
    val advertisingMode: AdvertisingMode = AdvertisingMode.LOW_POWER,
    val txPowerLevel: Int = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM,
    val testCondition: TestCondition = TestCondition.UNSPECIFIED,
)

enum class Role { ADVERTISER, SCANNER }
enum class ScanMode { LOW_POWER, BALANCED, LOW_LATENCY }
enum class AdvertisingMode { LOW_POWER, BALANCED, LOW_LATENCY }

// TestCondition.kt
@Serializable
data class TestCondition(
    val distanceMeters: Int? = null,
    val environment: Environment = Environment.UNSPECIFIED,
    val deviceState: DeviceState = DeviceState.UNSPECIFIED,
    val orientation: Orientation = Orientation.UNSPECIFIED,
    val pocketState: PocketState = PocketState.UNSPECIFIED,
)

enum class Environment { UNSPECIFIED, OPEN_INDOOR, CROWDED_INDOOR, APARTMENT_WALL, DIFFERENT_ROOM, DIFFERENT_FLOOR, OUTDOOR_OPEN }
enum class DeviceState { UNSPECIFIED, SCREEN_ON, SCREEN_OFF, LOCKED, BACKGROUNDED, REMOVED_FROM_RECENTS }
enum class Orientation { UNSPECIFIED, FACING_EACH_OTHER, BACK_TO_BACK, ONE_IN_POCKET, BOTH_IN_POCKET }
enum class PocketState { UNSPECIFIED, NOT_IN_POCKET, IN_POCKET }

// ExperimentRun.kt
@Serializable
data class ExperimentRun(
    val id: String,
    val config: ExperimentConfig,
    val startTime: Long,
    val endTime: Long? = null,
    val batteryStartPercent: Int? = null,
    val batteryEndPercent: Int? = null,
)

// ExperimentController.kt
interface ExperimentController {
    fun startExperiment(config: ExperimentConfig): Result<Unit>
    fun stopExperiment(): Result<Unit>
    fun getCurrentConfig(): ExperimentConfig?
    fun isRunning(): Boolean
    fun setTestCondition(condition: TestCondition)
    fun getCurrentRun(): ExperimentRun?
}
```

**Steps:**
- [x] Step 1: Write all data classes and enums with `@Serializable`
- [x] Step 2: Write `ExperimentController` interface
- [x] Step 3: Implement `ExperimentControllerImpl` coordinating `ServiceController` and `MeasurementRecorder`
- [x] Step 4: Persist current run to file for survival
- [x] Step 5: Build verify

---

### Phase H: Battery Monitor

#### Task H1: Battery Monitoring

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/battery/BatterySnapshot.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/battery/BatteryExperimentResult.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/battery/BatteryMonitor.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/battery/BatteryMonitorImpl.kt`

**Interfaces:**
```kotlin
// BatterySnapshot.kt
@Serializable
data class BatterySnapshot(
    val timestamp: Long,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val chargingType: Int,
    val temperature: Float,
    val voltage: Int,
    val health: Int,
)

// BatteryExperimentResult.kt
@Serializable
data class BatteryExperimentResult(
    val initialBatteryPercent: Int,
    val finalBatteryPercent: Int,
    val durationMinutes: Long,
    val scanConfig: String,
    val screenStateTimeline: List<ScreenStateEntry>,
    val deviceModel: String,
    val androidVersion: String,
    val approximateConsumptionRatePercentPerHour: Double,
)

@Serializable
data class ScreenStateEntry(
    val timestamp: Long,
    val state: DeviceState,
)

// BatteryMonitor.kt
interface BatteryMonitor {
    fun startMonitoring(experimentId: String): Result<Unit>
    fun stopMonitoring(): Result<BatteryExperimentResult>
    fun getCurrentSnapshot(): BatterySnapshot
    fun recordScreenStateChange(state: DeviceState)
}
```

**Requirements:**
- Records initial battery % at start, final at end
- Tracks screen state timeline
- Computes approximate consumption rate
- Exports device model, Android version
- Uses `BatteryManager` API

**Steps:**
- [ ] Step 1: Write data classes
- [ ] Step 2: Write `BatteryMonitor` interface
- [ ] Step 3: Implement `BatteryMonitorImpl` with `BroadcastReceiver` for battery changes
- [ ] Step 4: Write unit test for rate calculation (`BatteryRateTest.kt`)
- [ ] Step 5: Build verify

---

### Phase I: Export

#### Task I1: Experiment Exporter

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/export/ExperimentExporter.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/export/ExperimentExporterImpl.kt`

**Interfaces:**
```kotlin
// ExperimentExporter.kt
enum class ExportFormat { CSV, JSON, PLAIN_TEXT }

@Serializable
data class ExportResult(
    val fileUri: Uri,
    val format: ExportFormat,
    val recordCount: Int,
    val fileSizeBytes: Long,
)

interface ExperimentExporter {
    fun exportExperiment(experimentId: String, format: ExportFormat, context: Context): Result<ExportResult>
    fun exportAllExperiments(format: ExportFormat, context: Context): Result<ExportResult>
}
```

**Requirements:**
- CSV: headers + one row per detection record
- JSON: array of detection records + metadata
- Plain text: human-readable summary + records
- Uses `FileProvider` with `files-path` exports/
- No network

**Steps:**
- [x] Step 1: Write `ExperimentExporter` interface
- [ ] Step 2: Implement `ExperimentExporterImpl` with all three formats
- [x] Step 3: Build verify
- [ ] Step 4: Manual test export on device

---

### Phase J: Minimal Experiment UI (Compose)

#### Task J1: Theme & Components

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/theme/BleLabTheme.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/components/UtilitarianComponents.kt`

**Requirements:**
- Material3, utilitarian design, dense information
- No Ghost branding, no animations

**Steps:**
- [x] Step 1: Write `BleLabTheme.kt` with Material3 colors/typography
- [x] Step 2: Write reusable components (dense text, status indicators, etc.)
- [x] Step 3: Build verify

---

#### Task J2: Navigation & MainActivity

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/navigation/BleLabNavHost.kt`
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/MainActivity.kt`

**Screens (in order):**
1. RoleScreen
2. ControlScreen
3. TelemetryScreen
4. TestConditionScreen
5. ResultsScreen
6. BatteryScreen

**Steps:**
- [x] Step 1: Write `BleLabNavHost` with navigation graph
- [x] Step 2: Write `MainActivity` with Compose setup
- [x] Step 3: Build verify

---

#### Task J3: Role Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/RoleScreen.kt`

**UI:**
- Radio buttons: Advertiser / Scanner
- "Continue" button
- Utilitarian styling

**Steps:**
- [x] Step 1: Write `RoleScreen` Composable
- [x] Step 2: Connect to `ExperimentController` to set role
- [x] Step 3: Build verify

---

#### Task J4: Control Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/ControlScreen.kt`

**UI:**
- Start/Stop experiment button
- Rotation interval input (default 10 min)
- Scan mode dropdown (LOW_POWER/BALANCED/LOW_LATENCY)
- Advertising mode dropdown
- TX power dropdown
- Current experiment status display

**Steps:**
- [x] Step 1: Write `ControlScreen` Composable
- [x] Step 2: Connect to `ExperimentController.startExperiment()` / `stopExperiment()`
- [x] Step 3: Build verify
- [ ] Step 4: Manual test end-to-end: advertiser on device A, scanner on device B

---

#### Task J5: Telemetry Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/TelemetryScreen.kt`

**UI:**
- Current rotating test ID (hex)
- Advertising/Scanning status
- Detections count
- Latest RSSI
- Latest timestamp
- Elapsed time

**Steps:**
- [ ] Step 1: Write `TelemetryScreen` Composable
- [ ] Step 2: Observe live data from `MeasurementRecorder` / `ExperimentController`
- [ ] Step 3: Build verify

---

#### Task J6: Test Condition Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/TestConditionScreen.kt`

**UI:**
- Distance: dropdown (1, 3, 5, 10, 20, custom)
- Environment: dropdown (7 values)
- Device State: dropdown (6 values)
- Orientation: dropdown (5 values)
- Pocket State: dropdown (3 values)
- Apply button

**Steps:**
- [x] Step 1: Write `TestConditionScreen` Composable
- [x] Step 2: Connect to `ExperimentController.setTestCondition()`
- [x] Step 3: Build verify

---

#### Task J7: Results Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/ResultsScreen.kt`

**UI:**
- Total scans
- Detections
- Detection rate
- Average RSSI
- RSSI variance
- Min/Max RSSI
- Latency stats (min/max/avg/median)

**Steps:**
- [x] Step 1: Write `ResultsScreen` Composable
- [x] Step 2: Call `MeasurementRecorder.getAggregatedStats()`
- [x] Step 3: Build verify

---

#### Task J8: Battery Screen

**Files:**
- Create: `ble-feasibility-lab/src/main/java/com/ghost/blelab/ui/screens/BatteryScreen.kt`

**UI:**
- Starting battery %
- Current battery %
- Elapsed test time
- Approximate consumption rate (%/hour)
- Device model, Android version

**Steps:**
- [ ] Step 1: Write `BatteryScreen` Composable
- [ ] Step 2: Observe `BatteryMonitor` data
- [ ] Step 3: Build verify

---

### Phase K: Automated Tests (Pure Logic)

#### Task K1: Unit Test Suite

**Files:**
- Create: `ble-feasibility-lab/src/test/java/com/ghost/blelab/ephemeral/EphemeralIdGeneratorTest.kt`
- Create: `ble-feasibility-lab/src/test/java/com/ghost/blelab/experiment/AggregationTest.kt`
- Create: `ble-feasibility-lab/src/test/java/com/ghost/blelab/measurement/LatencyCalculationTest.kt`
- Create: `ble-feasibility-lab/src/test/java/com/ghost/blelab/battery/BatteryRateTest.kt`
- Create: `ble-feasibility-lab/src/test/java/com/ghost/blelab/util/JsonSerializationTest.kt`

**Test Coverage:**
- Ephemeral ID: rotation, determinism, uniqueness, length, no device info
- Aggregation: detection rate, RSSI stats, latency calculation
- Battery: consumption rate calculation
- JSON: round-trip serialization for all data classes

**Steps:**
- [ ] Step 1: Write all test classes with specific test cases
- [ ] Step 2: Run each test → verify RED
- [ ] Step 3: Ensure implementations pass (already done in prior phases)
- [ ] Step 4: Run full test suite → verify GREEN
- [ ] Step 5: Commit

**Commands:**
```bash
./gradlew :ble-feasibility-lab:test
```

---

### Phase L: Documentation

#### Task L1: Lab Documentation

**Files:**
- Create: `ble-feasibility-lab/docs/BLE_FEASIBILITY_LAB.md`

**Sections:**
1. Purpose
2. Architecture
3. Android Requirements
4. Permissions (with justification)
5. How to Build (Gradle commands from Termux)
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

**Steps:**
- [ ] Step 1: Write complete documentation
- [ ] Step 2: Verify build from Termux works
- [ ] Step 3: Commit

---

#### Task L2: Implementation Report

**Files:**
- Create: `ble-feasibility-lab/docs/BLE_FEASIBILITY_LAB_IMPLEMENTATION_REPORT.md`

**Sections (each labeled FACT/IMPLEMENTED/UNTESTED/UNKNOWN):**
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

**Steps:**
- [ ] Step 1: Write report after implementation complete
- [ ] Step 2: Verify all sections have proper labeling
- [ ] Step 3: Commit

---

### Phase M: Verification & Finalization

#### Task M1: Build & Test Verification

**Steps:**
- [x] Step 1: Clean build: `./gradlew :ble-feasibility-lab:clean assembleDebug`
- [x] Step 2: Run all unit tests: `./gradlew :ble-feasibility-lab:test`
- [ ] Step 3: Install on two physical devices (advertiser + scanner)
- [ ] Step 4: Verify foreground detection works
- [ ] Step 5: Verify background/screen-off/locked detection works
- [ ] Step 6: Verify rotation works (wait 10+ minutes)
- [ ] Step 7: Verify export works (CSV/JSON)
- [ ] Step 8: Verify battery monitoring records data
- [x] Step 9: Verify no Ghost web prototype files modified
- [x] Step 10: Verify no Constitution/Roadmap/Spec/Decisions modified

---

#### Task M2: Git Status & Summary

**Steps:**
- [ ] Step 1: `git status` — verify only `ble-feasibility-lab/` changed
- [ ] Step 2: `git diff --stat` — summary of changes
- [ ] Step 3: Present summary to user for review
- [ ] Step 4: Do NOT commit or push unless explicitly asked

---

## Dependency Additions (build.gradle.kts)

```kotlin
dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Bluetooth (API only, no extra deps)
    // BatteryManager, FileProvider — framework APIs

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}
```

---

## Success Criteria Checklist

> Reconciled 2026-08-19 against commit 125a998 (CI run 32213571392). Checked = verified by build/test/static analysis. Unchecked = physically unverified or not implemented.

- [ ] Two physical Android devices can advertise and detect one another — PHYSICALLY UNVERIFIED (no trials executed)
- [x] Rotating identifiers work (10-min default, configurable) — unit-tested (EphemeralIdGeneratorTest); physical rotation UNVERIFIED
- [x] No location permission requested — static manifest verification
- [x] Detection records contain no GPS or hardware identity — static code verification
- [ ] Foreground detection works — PHYSICALLY UNVERIFIED
- [ ] Background/locked testing can be performed — UNBLOCKED: Phase F implemented + build-verified (714b85c); physical trial pending
- [ ] Battery experiments can be run — BLOCKED: Phase H (battery monitor) not implemented
- [ ] Results can be exported (CSV/JSON/plain text) — CSV implemented + build-verified; JSON/plain text removed (commit 99acdf2); physical export UNVERIFIED
- [x] Deterministic logic has unit tests (all passing) — 74/74 passing, CI 32244881362
- [x] Lab remains isolated from Ghost product code — verified: zero diffs to src/, package.json, Vite/TS files since freeze commit 645cb99
- [ ] Documentation complete — PARTIAL: protocol + pilot report exist; lab README not written
- [ ] Implementation report complete with FACT/IMPLEMENTED/UNTESTED/UNKNOWN labels — NOT WRITTEN

---

## Execution Order Summary

| Phase | Tasks | Description |
|-------|-------|-------------|
| A | A1-A2 | Project skeleton + utilities |
| B | B1 | Ephemeral ID generator (TDD) |
| C | C1-C2 | BLE advertiser |
| D | D1 | BLE scanner |
| E | E1 | Detection recorder (file-based) |
| F | F1 | Foreground service |
| G | G1 | Experiment state & config |
| H | H1 | Battery monitor |
| I | I1 | Export |
| J | J1-J8 | Minimal Compose UI |
| K | K1 | Unit tests (pure logic) |
| L | L1-L2 | Documentation + report |
| M | M1-M2 | Verification + git summary |

---

**Ready to execute.** Each task produces independently testable deliverables. Vertical tracer bullet approach: complete one task fully (RED→GREEN→REFACTOR→commit) before moving to next.