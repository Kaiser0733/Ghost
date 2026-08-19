package com.ghost.blelab.util

/**
 * Chain a [Result] into another [Result]-producing operation.
 *
 * The Kotlin standard library [Result] provides `map`, `recover`, and `fold`,
 * but not `flatMap`. This extension fills that gap so Result-producing calls
 * can be composed without nesting `Result<Result<T>>`.
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(
        onSuccess = { transform(it) },
        onFailure = { Result.failure(it) }
    )
