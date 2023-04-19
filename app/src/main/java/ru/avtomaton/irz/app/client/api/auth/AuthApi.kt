package ru.avtomaton.irz.app.client.api.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.constants.AUTHENTICATION_AUTHENTICATE

/**
 * @author Anton Akkuzin
 */
interface AuthApi {

    @POST(AUTHENTICATION_AUTHENTICATE)
    suspend fun authenticate(@Body authBody: AuthBody) : Response<JwtTokens>
}
