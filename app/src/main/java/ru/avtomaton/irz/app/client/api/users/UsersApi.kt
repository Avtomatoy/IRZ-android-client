package ru.avtomaton.irz.app.client.api.users

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.avtomaton.irz.app.client.api.users.models.PositionDto
import ru.avtomaton.irz.app.client.api.users.models.UserDto
import ru.avtomaton.irz.app.constants.USERS
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface UsersApi {

    @GET("$USERS/me")
    suspend fun me(): Response<UserDto>

    @GET("$USERS/{id}")
    suspend fun user(@Path("id") id: UUID): Response<UserDto>

    @GET("/api/user_positions")
    suspend fun userPositions(@Query("userId") userId: UUID): Response<List<PositionDto>>

    @GET("/api/user_positions/my")
    suspend fun myUserPositions(): Response<List<PositionDto>>

    @POST("/api/subscriptions/subscribe")
    suspend fun subscribe(@Query("userId") userId: UUID): Response<Unit>

    @POST("/api/subscriptions/unsubscribe")
    suspend fun unsubscribe(@Query("userId") userId: UUID): Response<Unit>
}