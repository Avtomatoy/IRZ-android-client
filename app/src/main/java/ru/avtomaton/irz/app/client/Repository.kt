package ru.avtomaton.irz.app.client

import retrofit2.Response
import ru.avtomaton.irz.app.constants.HTTP_UNAUTHORIZED
import ru.avtomaton.irz.app.infra.SessionManager

/**
 * @author Anton Akkuzin
 */
open class Repository {

    fun <T> authWrap(response: Response<T>): Response<T> {
        SessionManager.setAuthenticated(response.code() != HTTP_UNAUTHORIZED)
        return response
    }
}
