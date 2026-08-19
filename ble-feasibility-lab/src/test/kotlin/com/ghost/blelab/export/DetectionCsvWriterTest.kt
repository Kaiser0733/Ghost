package com.ghost.blelab.export

import com.ghost.blelab.experiment.Environment
import com.ghost.blelab.experiment.TestCondition
import com.ghost.blelab.experiment.WallCondition
import com.ghost.blelab.measurement.DetectionRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [DetectionCsvWriter] — the pure CSV serialization used by
 * the experiment export. Verifies the protocol-required columns
 * (distance_m, environment_id, wall_condition) are present and correct.
 */
class DetectionCsvWriterTest {

    private fun record(
        condition: TestCondition,
        distanceLabel: Int? = condition.distanceMeters
    ) = DetectionRecord(
        localTimestamp = 1_000L,
        ephemeralId = ByteArray(16) { it.toByte() },
        rssi = -55,
        scanResultTimestamp = 2_000L,
        deviceLocalExperimentId = "run-1",
        testCondition = condition,
        distanceLabelMeters = distanceLabel
    )

    @Test
    fun `header contains protocol-required columns`() {
        val csv = DetectionCsvWriter.toCsv(emptyList())
        val header = csv.lines().first()
        assertTrue(header.contains("distance_m"))
        assertTrue(header.contains("environment_id"))
        assertTrue(header.contains("wall_condition"))
    }

    @Test
    fun `record row carries condition values`() {
        val condition = TestCondition(
            distanceMeters = 5,
            environment = Environment.APARTMENT_WALL,
            wallCondition = WallCondition.DRYWALL
        )
        val csv = DetectionCsvWriter.toCsv(listOf(record(condition)))
        val row = csv.lines()[1]

        assertTrue("row must contain distance 5", row.contains(",5,"))
        assertTrue("row must contain environment", row.contains("APARTMENT_WALL"))
        assertTrue("row must contain wall condition", row.contains("DRYWALL"))
        assertTrue("row must contain rssi", row.contains(",-55,"))
    }

    @Test
    fun `unspecified distance renders empty field`() {
        val condition = TestCondition(distanceMeters = null)
        val csv = DetectionCsvWriter.toCsv(listOf(record(condition, distanceLabel = null)))
        val row = csv.lines()[1]
        // distance field empty between experiment id and environment
        assertTrue(row.contains("run-1,,UNSPECIFIED"))
    }

    @Test
    fun `ephemeral id rendered as 32 hex chars`() {
        val csv = DetectionCsvWriter.toCsv(listOf(record(TestCondition())))
        val row = csv.lines()[1]
        val idField = row.split(",")[1]
        assertEquals(32, idField.length)
        assertTrue(idField.all { it in '0'..'9' || it in 'A'..'F' })
    }

    @Test
    fun `multiple records produce one row each`() {
        val records = listOf(record(TestCondition()), record(TestCondition()))
        val csv = DetectionCsvWriter.toCsv(records)
        // header + 2 rows + trailing newline -> 4 lines (last empty)
        assertEquals(4, csv.lines().size)
    }
}
