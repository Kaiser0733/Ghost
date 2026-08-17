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
        exportFormat: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords(experimentId, exportFormat, context, singleExperiment = true)
    }

    override fun exportAllExperiments(
        exportFormat: ExportFormat,
        context: Context
    ): Result<ExportResult> {
        return exportRecords("", exportFormat, context, singleExperiment = false)
    }

    private fun exportRecords(
        experimentId: String,
        exportFormat: ExportFormat,
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
            val files = experimentsDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.name.endsWith(".json")) {
                        val result = FileUtil.readJson(file)
                            .map { JsonUtil.listFromJson(DetectionRecord.serializer(), it) }
                            .getOrElse { emptyList<DetectionRecord>() }
                        allRecords.addAll(result)
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
                    writeExportFile(uri, records).map { bytesWritten: Int ->
                        ExportResult(
                            fileUri = uri,
                            format = ExportFormat.CSV,
                            recordCount = records.size,
                            fileSizeBytes = bytesWritten.toLong()
                        )
                    }
                )
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

            val bytesWritten = writeCsv(outputStream)
            outputStream.close()
            Result.success(bytesWritten.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun writeCsv(outputStream: OutputStream): Int {
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
}