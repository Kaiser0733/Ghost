package com.ghost.blelab.util

import org.junit.Assert.*
import org.junit.Test

class FileUtilTest {

    @Test
    fun testWriteAndReadJson() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testFile = java.io.File(tempDir, "test_write_read.json")
        
        val content = """{"key": "value", "number": 42}"""
        
        val writeResult = FileUtil.writeJson(testFile, content)
        assertTrue("Write should succeed", writeResult.isSuccess)
        
        val readResult = FileUtil.readJson(testFile)
        assertTrue("Read should succeed", readResult.isSuccess)
        assertEquals("Content should match", content, readResult.getOrThrow())
        
        testFile.delete()
    }

    @Test
    fun testWriteJsonCreatesParentDirectories() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testFile = java.io.File(tempDir, "nested/deep/path/test.json")
        
        val content = """{"test": true}"""
        val writeResult = FileUtil.writeJson(testFile, content)
        
        assertTrue("Write should succeed with nested directories", writeResult.isSuccess)
        assertTrue("File should exist", testFile.exists())
        
        val readResult = FileUtil.readJson(testFile)
        assertTrue("Read should succeed", readResult.isSuccess)
        assertEquals("Content should match", content, readResult.getOrThrow())
        
        testFile.parentFile!!.deleteRecursively()
    }

    @Test
    fun testReadJsonNonExistentFile() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testFile = java.io.File(tempDir, "nonexistent.json")
        
        val readResult = FileUtil.readJson(testFile)
        assertFalse("Read should fail for non-existent file", readResult.isSuccess)
    }

    @Test
    fun testAppendCsvCreatesFileWithHeader() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testFile = java.io.File(tempDir, "test.csv")
        
        val header = "timestamp,value"
        val line1 = "1000,42"
        val line2 = "2000,84"
        
        val result1 = FileUtil.appendCsv(testFile, line1, header)
        assertTrue("First append should succeed", result1.isSuccess)
        
        val result2 = FileUtil.appendCsv(testFile, line2)
        assertTrue("Second append should succeed", result2.isSuccess)
        
        val content = testFile.readText()
        val lines = content.lines().toList()
        assertEquals("Should have 3 lines (header + 2 data)", 3, lines.size)
        assertEquals("First line should be header", header, lines[0])
        assertEquals("Second line should be first data", line1, lines[1])
        assertEquals("Third line should be second data", line2, lines[2])
        
        testFile.delete()
    }

    @Test
    fun testAppendCsvWithoutHeader() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testFile = java.io.File(tempDir, "test_no_header.csv")
        
        val line1 = "1000,42"
        val line2 = "2000,84"
        
        val result1 = FileUtil.appendCsv(testFile, line1)
        assertTrue("First append should succeed", result1.isSuccess)
        
        val result2 = FileUtil.appendCsv(testFile, line2)
        assertTrue("Second append should succeed", result2.isSuccess)
        
        val content = testFile.readText()
        val lines = content.lines().toList()
        assertEquals("Should have 2 lines (no header)", 2, lines.size)
        assertEquals("First line should be data", line1, lines[0])
        assertEquals("Second line should be data", line2, lines[1])
        
        testFile.delete()
    }

    @Test
    fun testEnsureDir() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testDir = java.io.File(tempDir, "new/dir/structure")
        
        val result = FileUtil.ensureDir(testDir)
        assertTrue("Ensure dir should succeed", result.isSuccess)
        assertTrue("Directory should exist", testDir.exists())
        assertTrue("Should be directory", testDir.isDirectory)
        
        testDir.deleteRecursively()
    }

    @Test
    fun testListExperimentFiles() {
        val tempDir = java.io.File.createTempFile("test", "").parentFile!!
        val testDir = java.io.File(tempDir, "experiments")
        
        java.io.File(testDir, "exp1.json").createNewFile()
        java.io.File(testDir, "exp2.json").createNewFile()
        java.io.File(testDir, "readme.txt").createNewFile()
        java.io.File(testDir, "exp3.json").createNewFile()
        
        val files = FileUtil.listExperimentFiles(testDir, ".json")
        assertEquals("Should find 3 JSON files", 3, files.size)
        assertTrue("All should be .json files", files.all { it.name.endsWith(".json") })
        
        testDir.deleteRecursively()
    }
}