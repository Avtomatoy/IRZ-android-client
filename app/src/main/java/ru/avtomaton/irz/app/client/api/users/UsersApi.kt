package ru.avtomaton.irz.app.client.api.users

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.avtomaton.irz.app.client.api.users.models.UserDto
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface UsersApi {

    @GET("users/me")
    suspend fun me() : Response<UserDto>

    @GET("users/{id}")
    suspend fun user(@Path("id") id: UUID) : Response<UserDto>
}