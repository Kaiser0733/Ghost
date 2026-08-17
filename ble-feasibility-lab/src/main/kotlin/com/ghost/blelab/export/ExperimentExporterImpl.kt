package com.ghost.blelab.export

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.ghost.blelab.measurement.DetectionRecord
import com.ghost.blelab.measurement.MeasurementRecorder
import com.ghost.blelab.util.FileUtil
import com.ghost.blelab.util.JsonUtil
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.Result

class ExperimentExporterImpl(
    private val context: Context,
    private val measurementRecorder: MeasurementRecorder
) : ExperimentExporter {

    private val json = Json { prettyPrint = true }

    override fun exportExperiment(
        experimentId: String,
        format: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords(experimentId, format, context, singleExperiment = true)
    }

    override fun exportAllExperiments(
        format: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords("", format, context, singleExperiment = false)
    }

    private fun exportRecords(
        experimentId: String,
        format: ExportFormat,
        context: Context,
        singleExperiment: Boolean
    ): Result<ExportResult> {
        val recordsResult: Result<List<DetectionRecord>> = if (singleExperiment) {
            measurementRecorder.getAllRecords(experimentId)
        } else {
            // Get all experiment files
            val experimentsDir = FileUtil.ensureDir(
                File(FileUtil.getFilesDir(context), "experiments")
            ).getOrThrow()

            val allRecords = mutableListOf<DetectionRecord>()
            val files: Array<File> = experimentsDir.listFiles()?.filter { it.name.endsWith(".json") } ?: emptyArray()
            for (file in files) {
                val result = FileUtil.readJson(file)
                    .map { JsonUtil.listFromJson(DetectionRecord.serializer(), it) }
                    .getOrElse { emptyList<DetectionRecord>() }
                allRecords.addAll(result)
            }
            Result.success(allRecords)
        }

        return recordsResult.flatMap { records: List<DetectionRecord> ->
            if (records.isEmpty()) {
                Result.failure(IllegalStateException("No records to export"))
            } else {
                val fileName = generateFileName(format, singleExperiment, experimentId)
                val fileResult = createExportFile(context, fileName, format)

                fileResult.flatMap { uri: Uri ->
                    writeExportFile(uri, records, format).map { bytesWritten: Int ->
                        ExportResult(
                            fileUri = uri,
                            format = format,
                            recordCount = records.size,
                            fileSizeBytes = bytesWritten.toLong()
                        )
                    }
                }
            }
        }
    }

    private fun generateFileName(format: ExportFormat, singleExperiment: Boolean, experimentId: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val prefix = if (singleExperiment) "experiment_$experimentId" else "all_experiments"
        val extension = when (format) {
            ExportFormat.CSV -> "csv"
            ExportFormat.JSON -> "json"
            ExportFormat.PLAIN_TEXT -> "txt"
        }
        return "${prefix}_$timestamp.$extension"
    }

    private fun createExportFile(context: Context, fileName: String, format: ExportFormat): Result<Uri> {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, when (format) {
                ExportFormat.CSV -> "text/csv"
                ExportFormat.JSON -> "application/json"
                ExportFormat.PLAIN_TEXT -> "text/plain"
            })
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BLE_Feasibility_Lab/")
        }

        return try {
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) Result.success(uri) else Result.failure(IOException("Failed to create export file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeExportFile(uri: Uri, records: List<DetectionRecord>, format: ExportFormat): Result<Long> {
        return try {
            val outputStream = context.contentResolver.openOutputStream(uri)
                ?: return Result.failure(IOException("Failed to open output stream"))

            val bytesWritten = when (format) {
                ExportFormat.JSON -> writeJson(outputStream, records)
                ExportFormat.CSV -> writeCsv(outputStream, records)
                ExportFormat.PLAIN_TEXT -> writePlainText(outputStream, records)
            }
            outputStream.close()
            Result.success(bytesWritten.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeJson(outputStream: OutputStream, records: List<DetectionRecord>): Int {
        val jsonString = JsonUtil.listToJson(DetectionRecord.serializer(), records)
        val bytes = jsonString.toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        outputStream.write(bytes)
        return bytes.size
    }

    private fun writeCsv(outputStream: OutputStream, records: List<DetectionRecord>): Int {
        val header = "localTimestamp,ephemeralId,rssi,scanResultTimestamp,deviceLocalExperimentId,distanceLabelMeters\n"
        val csvString = StringBuilder(header)

        for (record in records) {
            val ephemeralIdHex = record.ephemeralId.joinToString("") { "%02X".format(it) }
            val distanceLabel = record.distanceLabelMeters?.toString() ?? ""
            csvString.append("${record.localTimestamp},$ephemeralIdHex,${record.rssi},${record.scanResultTimestamp},${record.deviceLocalExperimentId},$distanceLabel\n")
        }

        val bytes = csvString.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        outputStream.write(bytes)
        return bytes.size
    }

    private fun writePlainText(outputStream: OutputStream, records: List<DetectionRecord>): Int {
        val builder = StringBuilder()
        builder.append("BLE Feasibility Lab - Export\n")
        builder.append("Generated: %s\n".format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())))
        builder.append("Records: %d\n\n".format(records.size))

        for (record in records) {
            val ephemeralIdHex = record.ephemeralId.joinToString("") { "%02X".format(it) }
            builder.append("Timestamp: " + record.localTimestamp + "\n")
            builder.append("  Ephemeral ID: " + ephemeralIdHex + "\n")
            builder.append("  RSSI: " + record.rssi + " dBm\n")
            builder.append("  Scan Timestamp: " + record.scanResultTimestamp + "\n")
            builder.append("  Experiment ID: " + record.deviceLocalExperimentId + "\n")
            builder.append("  Distance Label: " + (record.distanceLabelMeters?.toString() ?: "N/A") + "m\n\n")
        }

        val bytes = builder.toString().toByteArray(java.nio.charset.StandardCharsets.UTF_8)
        outputStream.write(bytes)
        return bytes.size
    }
}