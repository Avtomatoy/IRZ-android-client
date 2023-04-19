package ru.avtomaton.irz.app.client.api.auth

import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody

/**
 * @author Anton Akkuzin
 */
object AuthRepository {

    suspend fun auth(authBody: AuthBody): Result<Boolean> {
        return try {
            val response = IrzClient.authApi.authenticate(authBody)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.success(false)
            }
        } catch (ex: Throwable) {
            Result.failure(ex)
        }
    }
}