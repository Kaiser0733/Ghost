package com.ghost.blelab.export

import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
enum class ExportFormat {
    CSV, JSON, PLAIN_TEXT
}

@Serializable
data class ExportResult(
    @Contextual val fileUri: Uri,
    val format: ExportFormat,
    val recordCount: Int,
    val fileSizeBytes: Long,
)

interface ExperimentExporter {
    fun exportExperiment(
        experimentId: String,
        format: ExportFormat,
        context: android.content.Context
    ): Result<ExportResult>
    fun exportAllExperiments(
        format: ExportFormat,
        context: android.content.Context
    ): Result<ExportResult>
}