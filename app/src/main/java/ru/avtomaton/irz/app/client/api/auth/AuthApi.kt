package ru.avtomaton.irz.app.client.api.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.client.authentication_authenticate

/**
 * @author Anton Akkuzin
 */
interface AuthApi {

    @POST(authentication_authenticate)
    suspend fun authenticate(@Body authBody: AuthBody) : Response<JwtTokens>
}
