package com.ghost.blelab.export

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.ghost.blelab.measurement.DetectionRecord
import com.ghost.blelab.measurement.MeasurementRecorder
import com.ghost.blelab.util.FileUtil
import com.ghost.blelab.util.JsonUtil
import com.ghost.blelab.util.flatMap
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExperimentExporterImpl(
    private val context: Context,
    private val measurementRecorder: MeasurementRecorder
) : ExperimentExporter {

    override fun exportExperiment(
        experimentId: String,
        exportFormat: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords(experimentId, context, singleExperiment = true)
    }

    override fun exportAllExperiments(
        exportFormat: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords("", context, singleExperiment = false)
    }

    private fun exportRecords(
        experimentId: String,
        context: Context,
        singleExperiment: Boolean
    ): Result<ExportResult> {
        val recordsResult: Result<List<DetectionRecord>> = if (singleExperiment) {
            measurementRecorder.getAllRecords(experimentId)
        } else {
            // Get all experiment files
            val experimentsDir = File(FileUtil.getFilesDir(context), "experiments")
            FileUtil.ensureDir(experimentsDir)

            val allRecords = mutableListOf<DetectionRecord>()
            val files: Array<File>? = experimentsDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.name.endsWith(".json")) {
                        val records: List<DetectionRecord> = FileUtil.readJson(file)
                            .flatMap { JsonUtil.listFromJson(DetectionRecord.serializer(), it) }
                            .getOrElse { emptyList() }
                        allRecords.addAll(records)
                    }
                }
            }
            Result.success(allRecords)
        }

        return recordsResult.flatMap { records: List<DetectionRecord> ->
            if (records.isEmpty()) {
                Result.failure(IllegalStateException("No records to export"))
            } else {
                val fileName = generateFileName(records.size)
                val fileResult = createExportFile(context, fileName)

                fileResult.flatMap { uri: Uri ->
                    writeExportFile(uri, records).map { bytesWritten: Long ->
                        ExportResult(
                            fileUri = uri,
                            format = ExportFormat.CSV,
                            recordCount = records.size,
                            fileSizeBytes = bytesWritten
                        )
                    }
                }
            }
        }
    }

    private fun generateFileName(recordCount: Int): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "ble_experiment_${recordCount}_records_$timestamp.csv"
    }

    private fun createExportFile(context: Context, fileName: String): Result<Uri> {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BLE_Feasibility_Lab/")
        }

        return try {
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) Result.success(uri) else Result.failure(IOException("Failed to create export file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeExportFile(uri: Uri, records: List<DetectionRecord>): Result<Long> {
        return try {
            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: return Result.failure(IOException("Failed to open output stream"))

            val bytesWritten = writeCsv(outputStream, records)
            outputStream.close()
            Result.success(bytesWritten.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeCsv(outputStream: OutputStream, records: List<DetectionRecord>): Int {
        val bytes = DetectionCsvWriter.toCsv(records)
            .toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        outputStream.write(bytes)
        return bytes.size
    }
}

/**
 * Pure CSV serialization for detection records — no Android dependencies,
 * unit-testable on the JVM.
 *
 * Columns per BLE_FEASIBILITY_EXPERIMENT_PROTOCOL.md section 8:
 * distance_m, environment_id and wall_condition are protocol-required
 * manual-condition fields carried on each detection's test condition.
 */
object DetectionCsvWriter {

    fun toCsv(records: List<DetectionRecord>): String {
        val header = "localTimestamp,ephemeralId,rssi,scanResultTimestamp,deviceLocalExperimentId," +
            "distance_m,environment_id,wall_condition\n"
        val csvString = StringBuilder(header)

        for (record in records) {
            val ephemeralIdHex = record.ephemeralId.joinToString("") { "%02X".format(it) }
            val distanceLabel = record.distanceLabelMeters?.toString() ?: ""
            val environment = record.testCondition.environment.name
            val wall = record.testCondition.wallCondition.name
            csvString.append(
                "${record.localTimestamp},$ephemeralIdHex,${record.rssi}," +
                    "${record.scanResultTimestamp},${record.deviceLocalExperimentId}," +
                    "$distanceLabel,$environment,$wall\n"
            )
        }
        return csvString.toString()
    }
}
