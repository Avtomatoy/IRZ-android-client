package ru.avtomaton.irz.app.model

/**
 * @author Anton Akkuzin
 */
sealed class OpResult<T>(
    private val value: T? = null,
    val isOk: Boolean,
    val isFailure: Boolean = !isOk
) {

    class Success<T>(value: T): OpResult<T>(value = value, isOk = true)

    class Failure<T> : OpResult<T>(isOk = false)

    fun value(): T {
        return value!!
    }
}