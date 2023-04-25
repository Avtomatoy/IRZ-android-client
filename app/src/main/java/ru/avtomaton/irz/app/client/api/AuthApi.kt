package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.avtomaton.irz.app.model.pojo.Credentials
import ru.avtomaton.irz.app.model.pojo.JwtTokens
import ru.avtomaton.irz.app.constants.AUTHENTICATION_AUTHENTICATE

/**
 * @author Anton Akkuzin
 */
interface AuthApi {

    @POST(AUTHENTICATION_AUTHENTICATE)
    suspend fun authenticate(@Body credentials: Credentials): Response<JwtTokens>
}
