package com.ghost.blelab.measurement

import kotlinx.serialization.Serializable
import java.util.UUID

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

interface MeasurementRecorder {
    fun recordDetection(record: DetectionRecord): Result<Unit>
    fun getAggregatedStats(experimentId: String): Result<AggregatedStats>
    fun getAllRecords(experimentId: String): Result<List<DetectionRecord>>
    fun clearExperiment(experimentId: String): Result<Unit>
}