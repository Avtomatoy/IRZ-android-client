package ru.avtomaton.irz.app.model.repository

import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.pojo.Email

/**
 * @author Anton Akkuzin
 */
object AuthRepository : Repository() {

    suspend fun resetPassword(email: Email): Boolean {
        return tryForSuccess {
            IrzHttpClient.authApi.resetPassword(email).isSuccessful
        }
    }
}
