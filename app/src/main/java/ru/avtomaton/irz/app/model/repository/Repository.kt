package ru.avtomaton.irz.app.model.repository

import ru.avtomaton.irz.app.model.OpResult

/**
 * @author Anton Akkuzin
 */
open class Repository {

    protected suspend fun <T> tryForResult(block: suspend () -> OpResult<T>): OpResult<T> {
        return try {
            block()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }

    protected suspend fun tryForSuccess(block: suspend () -> Boolean): Boolean {
        return try {
            block()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }
}