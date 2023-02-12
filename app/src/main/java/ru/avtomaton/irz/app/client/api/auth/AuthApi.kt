package ru.avtomaton.irz.app.client.api.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.AuthResponse

/**
 * @author Anton Akkuzin
 */
interface AuthApi {

    @POST("/authentication/authenticate")
    fun authenticate(@Body authBody: AuthBody) : Call<AuthResponse>
}
