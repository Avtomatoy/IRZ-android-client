package ru.avtomaton.irz.app.model.repository

import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.pojo.Email

/**
 * @author Anton Akkuzin
 */
object AuthRepository {

    suspend fun resetPassword(email: Email): Boolean {
        return try {
            val response = IrzHttpClient.authApi.resetPassword(email)
            response.isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }
}