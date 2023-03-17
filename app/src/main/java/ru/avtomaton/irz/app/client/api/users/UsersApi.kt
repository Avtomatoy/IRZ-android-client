package ru.avtomaton.irz.app.client.api.users

import retrofit2.Call
import retrofit2.http.GET
import ru.avtomaton.irz.app.client.api.users.models.MeResponse

/**
 * @author Anton Akkuzin
 */
interface UsersApi {

    @GET("users/me")
    fun me() : Call<MeResponse>
}