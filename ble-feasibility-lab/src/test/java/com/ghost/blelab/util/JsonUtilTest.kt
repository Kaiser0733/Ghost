package com.ghost.blelab.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test

@Serializable
data class TestData(
    val id: String,
    val value: Int,
    val flag: Boolean
)

@Serializable
data class TestDataList(
    val items: List<TestData>
)

class JsonUtilTest {

    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        prettyPrint = true
    }

    @Test
    fun testToJson() {
        val data = TestData("test-id", 42, true)
        val jsonString = JsonUtil.toJson(TestData.serializer(), data)
        
        assertNotNull("JSON should not be null", jsonString)
        assertTrue("JSON should contain id", jsonString.contains("test-id"))
        assertTrue("JSON should contain value", jsonString.contains("42"))
        assertTrue("JSON should contain flag", jsonString.contains("true"))
    }

    @Test
    fun testFromJsonSuccess() {
        val jsonString = """{"id": "test-id", "value": 42, "flag": true}"""
        val result = JsonUtil.fromJson(TestData.serializer(), jsonString)
        
        assertTrue("Deserialization should succeed", result.isSuccess)
        val data = result.getOrThrow()
        assertEquals("ID should match", "test-id", data.id)
        assertEquals("Value should match", 42, data.value)
        assertEquals("Flag should match", true, data.flag)
    }

    @Test
    fun testFromJsonFailure() {
        val invalidJson = """{"id": "test", "value": "not-a-number"}"""
        val result = JsonUtil.fromJson(TestData.serializer(), invalidJson)
        
        assertFalse("Deserialization should fail for invalid JSON", result.isSuccess)
    }

    @Test
    fun testListToJson() {
        val list = listOf(
            TestData("item1", 1, true),
            TestData("item2", 2, false),
            TestData("item3", 3, true)
        )
        val jsonString = JsonUtil.listToJson(TestDataList.serializer(), TestDataList(list))
        
        assertNotNull("JSON should not be null", jsonString)
        assertTrue("JSON should contain all items", jsonString.contains("item1") && jsonString.contains("item2") && jsonString.contains("item3"))
    }

    @Test
    fun testListFromJson() {
        val jsonString = """{"items": [{"id": "item1", "value": 1, "flag": true}, {"id": "item2", "value": 2, "flag": false}]}"""
        val result = JsonUtil.listFromJson(TestDataList.serializer(), jsonString)
        
        assertTrue("Deserialization should succeed", result.isSuccess)
        val data = result.getOrThrow()
        assertEquals("Should have 2 items", 2, data.items.size)
        assertEquals("First item ID", "item1", data.items[0].id)
        assertEquals("Second item ID", "item2", data.items[1].id)
    }

    @Test
    fun testRoundTrip() {
        val original = TestData("roundtrip-test", 123, false)
        val jsonString = JsonUtil.toJson(TestData.serializer(), original)
        val result = JsonUtil.fromJson(TestData.serializer(), jsonString)
        
        assertTrue("Round-trip should succeed", result.isSuccess)
        val restored = result.getOrThrow()
        assertEquals("ID should match", original.id, restored.id)
        assertEquals("Value should match", original.value, restored.value)
        assertEquals("Flag should match", original.flag, restored.flag)
    }
}