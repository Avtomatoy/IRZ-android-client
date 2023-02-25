package ru.avtomaton.irz.app.client.api.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.ChangePasswordBody
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens

/**
 * @author Anton Akkuzin
 */
interface AuthApi {

    @POST("authentication/authenticate")
    fun authenticate(@Body authBody: AuthBody) : Call<JwtTokens>

    @POST("authentication/refresh")
    fun refresh(@Body jwtTokens: JwtTokens) : Call<JwtTokens>

    @PUT("authentication/change_password")
    fun changePassword(@Body changePasswordBody: ChangePasswordBody) : Call<Void>
}
