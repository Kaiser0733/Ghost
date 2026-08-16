package com.ghost.blelab.util

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object FileUtil {

    /**
     * Atomically write JSON content to a file using a temporary file + rename.
     */
    fun writeJson(file: File, content: String): Result<Unit> = try {
        file.parentFile?.mkdirs()
        val tempFile = File(file.parentFile, "${file.name}.tmp")
        FileOutputStream(tempFile).use { it.write(content.toByteArray(StandardCharsets.UTF_8)) }
        if (!tempFile.renameTo(file)) {
            Result.failure(IOException("Failed to rename temp file to $file"))
        } else {
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Read JSON content from a file.
     */
    fun readJson(file: File): Result<String> = try {
        if (!file.exists()) {
            Result.failure(IllegalStateException("File does not exist: $file"))
        } else {
            Result.success(file.readText(StandardCharsets.UTF_8))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Append a CSV line to a file, creating with header if needed.
     */
    fun appendCsv(file: File, line: String, header: String? = null): Result<Unit> = try {
        file.parentFile?.mkdirs()
        val needsHeader = header != null && (!file.exists() || file.length() == 0L)
        FileOutputStream(file, true).use { out ->
            if (needsHeader) {
                out.write("$header\n".toByteArray(StandardCharsets.UTF_8))
            }
            out.write("$line\n".toByteArray(StandardCharsets.UTF_8))
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Ensure a directory exists.
     */
    fun ensureDir(dir: File): Result<Unit> = try {
        if (dir.mkdirs() || dir.exists()) {
            Result.success(Unit)
        } else {
            Result.failure(IOException("Failed to create directory: $dir"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * List all experiment files in a directory matching a pattern.
     */
    fun listExperimentFiles(dir: File, extension: String = ".json"): List<File> {
        return try {
            if (!dir.exists()) return emptyList()
            dir.listFiles()?.filter { it.isFile && it.name.endsWith(extension) }?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}