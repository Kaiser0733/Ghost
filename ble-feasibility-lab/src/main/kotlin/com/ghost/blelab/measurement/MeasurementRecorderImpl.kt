package com.ghost.blelab.measurement

import com.ghost.blelab.experiment.TestCondition
import com.ghost.blelab.util.FileUtil
import com.ghost.blelab.util.JsonUtil
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.UUID
import kotlin.Result
import kotlin.math.pow

class MeasurementRecorderImpl(private val context: android.content.Context) : MeasurementRecorder {

    private val experimentsDir: File by lazy {
        val dir = File(FileUtil.getFilesDir(context), "experiments")
        FileUtil.ensureDir(dir).getOrThrow()
        dir
    }

    private val json = Json { prettyPrint = true }

    override fun recordDetection(record: DetectionRecord): Result<Unit> {
        val experimentFile = File(experimentsDir, "${record.deviceLocalExperimentId}.json")
        
        val existingRecords: List<DetectionRecord> = if (experimentFile.exists()) {
            FileUtil.readJson(experimentFile).getOrElse { "[]" }
                .let { JsonUtil.listFromJson(DetectionRecord.serializer(), it).getOrElse { emptyList() } }
        } else {
            emptyList()
        }

        val updatedRecords = existingRecords + record
        val jsonString = JsonUtil.listToJson(DetectionRecord.serializer(), updatedRecords)
        
        return FileUtil.writeJson(experimentFile, jsonString)
    }

    override fun getAggregatedStats(experimentId: String): Result<AggregatedStats> {
        val experimentFile = File(experimentsDir, "${experimentId}.json")
        
        if (!experimentFile.exists()) {
            return Result.failure(IllegalStateException("Experiment not found: $experimentId"))
        }

        val recordsResult = FileUtil.readJson(experimentFile)
            .map { JsonUtil.listFromJson(DetectionRecord.serializer(), it) }

        return recordsResult.flatMap { records: List<DetectionRecord> ->
            if (records.isEmpty()) {
                Result.success(AggregatedStats(
                    totalScans = 0,
                    detections = 0,
                    detectionRate = 0.0,
                    averageRssi = 0.0,
                    rssiVariance = 0.0,
                    minRssi = 0,
                    maxRssi = 0,
                    latencyStats = LatencyStats(0, 0, 0.0, 0)
                ))
            } else {
                val rssiValues = records.map { it.rssi }
                val totalScans = records.size.toLong()
                val detections = records.size.toLong()
                val averageRssi = rssiValues.average()
                val rssiVariance = rssiValues.map { (it - averageRssi).toDouble().pow(2) }.average()
                val minRssi = rssiValues.minOrNull() ?: 0
                val maxRssi = rssiValues.maxOrNull() ?: 0

                // Simple latency: use scanResultTimestamp for first/last detection
                val timestamps = records.map { it.scanResultTimestamp }.sorted()
                val minLatency = if (timestamps.size > 1) (timestamps[1] - timestamps[0]) / 1_000_000 else 0L
                val maxLatency = if (timestamps.size > 1) (timestamps.last() - timestamps.first()) / 1_000_000 else 0L
                val avgLatency = if (timestamps.size > 1) (timestamps.last() - timestamps.first()) / 1_000_000.0 / (timestamps.size - 1) else 0.0
                val medianLatency = if (timestamps.size > 1) (timestamps[timestamps.size / 2] - timestamps[timestamps.size / 2 - 1]) / 1_000_000 else 0L

                Result.success(AggregatedStats(
                    totalScans = totalScans,
                    detections = detections,
                    detectionRate = 1.0,
                    averageRssi = averageRssi,
                    rssiVariance = rssiVariance,
                    minRssi = minRssi,
                    maxRssi = maxRssi,
                    latencyStats = LatencyStats(
                        minLatencyMs = minLatency,
                        maxLatencyMs = maxLatency,
                        avgLatencyMs = avgLatency,
                        medianLatencyMs = medianLatency
                    )
                ))
            }
        }
    }

    override fun getAllRecords(experimentId: String): Result<List<DetectionRecord>> {
        val experimentFile = File(experimentsDir, "${experimentId}.json")
        
        if (!experimentFile.exists()) {
            return Result.success(emptyList<DetectionRecord>())
        }

        return FileUtil.readJson(experimentFile)
            .map { JsonUtil.listFromJson(DetectionRecord.serializer(), it) }
            .getOrElse { emptyList<DetectionRecord>() }
    }

    override fun clearExperiment(experimentId: String): Result<Unit> {
        val experimentFile = File(experimentsDir, "${experimentId}.json")
        if (experimentFile.exists()) {
            return if (experimentFile.delete()) Result.success(Unit) else Result.failure(IOException("Failed to delete"))
        }
        return Result.success(Unit)
    }
}