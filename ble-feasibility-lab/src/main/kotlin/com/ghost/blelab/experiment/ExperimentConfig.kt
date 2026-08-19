package com.ghost.blelab.experiment

import kotlinx.serialization.Serializable

@Serializable
data class ExperimentConfig(
    val role: Role = Role.UNSPECIFIED,
    val rotationIntervalMinutes: Int = 10,
    val scanMode: ScanMode = ScanMode.LOW_POWER,
    val advertisingMode: AdvertisingMode = AdvertisingMode.LOW_POWER,
    val txPowerLevel: Int = android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM,
    val testCondition: TestCondition = TestCondition.UNSPECIFIED,
)

@Serializable
enum class Role {
    UNSPECIFIED,
    ADVERTISER,
    SCANNER
}

@Serializable
enum class ScanMode {
    LOW_POWER,
    BALANCED,
    LOW_LATENCY
}

@Serializable
enum class AdvertisingMode {
    LOW_POWER,
    BALANCED,
    LOW_LATENCY
}

@Serializable
data class TestCondition(
    val distanceMeters: Int? = null,
    val environment: Environment = Environment.UNSPECIFIED,
    val deviceState: DeviceState = DeviceState.UNSPECIFIED,
    val orientation: Orientation = Orientation.UNSPECIFIED,
    val pocketState: PocketState = PocketState.UNSPECIFIED,
    val wallCondition: WallCondition = WallCondition.UNSPECIFIED,
) {
    companion object {
        val UNSPECIFIED = TestCondition()
    }
}

@Serializable
enum class Environment {
    UNSPECIFIED,
    OPEN_INDOOR,
    CROWDED_INDOOR,
    APARTMENT_WALL,
    DIFFERENT_ROOM,
    DIFFERENT_FLOOR,
    OUTDOOR_OPEN
}

@Serializable
enum class DeviceState {
    UNSPECIFIED,
    SCREEN_ON,
    SCREEN_OFF,
    LOCKED,
    BACKGROUNDED,
    REMOVED_FROM_RECENTS
}

@Serializable
enum class Orientation {
    UNSPECIFIED,
    FACING_EACH_OTHER,
    BACK_TO_BACK,
    ONE_IN_POCKET,
    BOTH_IN_POCKET
}

@Serializable
enum class PocketState {
    UNSPECIFIED,
    NOT_IN_POCKET,
    IN_POCKET
}

/**
 * Wall condition per the physical experiment protocol
 * (BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md: wall_condition =
 * "none" / "drywall" / "concrete"). UNSPECIFIED means not recorded.
 */
@Serializable
enum class WallCondition {
    UNSPECIFIED,
    NONE,
    DRYWALL,
    CONCRETE
}

@Serializable
data class ExperimentRun(
    val id: String,
    val config: ExperimentConfig,
    val startTime: Long,
    val endTime: Long? = null,
    val batteryStartPercent: Int? = null,
    val batteryEndPercent: Int? = null,
)

interface ExperimentController {
    fun startExperiment(config: ExperimentConfig): Result<Unit>
    fun stopExperiment(): Result<Unit>
    fun getCurrentConfig(): ExperimentConfig?
    fun isRunning(): Boolean
    fun setTestCondition(condition: TestCondition)
    fun getCurrentRun(): ExperimentRun?
}