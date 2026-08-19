package com.ghost.blelab.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
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
     * Serialize list to JSON string.
     */
    fun <T> listToJson(elementSerializer: KSerializer<T>, value: List<T>): String =
        json.encodeToString(ListSerializer(elementSerializer), value)

    /**
     * Deserialize JSON string to list.
     */
    fun <T> listFromJson(elementSerializer: KSerializer<T>, jsonString: String): Result<List<T>> = try {
        Result.success(json.decodeFromString(ListSerializer(elementSerializer), jsonString))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
