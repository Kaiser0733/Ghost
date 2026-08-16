package com.ghost.blelab.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object JsonUtil {
    
    private val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        prettyPrint = true
    }

    /**
     * Serialize any object to JSON string.
     */
    fun <T> toJson(serializer: KSerializer<T>, value: T): String = json.encodeToString(serializer, value)

    /**
     * Deserialize JSON string to object.
     */
    fun <T> fromJson(serializer: KSerializer<T>, jsonString: String): Result<T> = try {
        Result.success(json.decodeFromString(serializer, jsonString))
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Serialize list wrapper to JSON string.
     */
    fun <T> listToJson(serializer: KSerializer<T>, value: T): String = json.encodeToString(serializer, value)

    /**
     * Deserialize JSON string to list wrapper.
     */
    fun <T> listFromJson(serializer: KSerializer<T>, jsonString: String): Result<T> = try {
        Result.success(json.decodeFromString(serializer, jsonString))
    } catch (e: Exception) {
        Result.failure(e)
    }
}