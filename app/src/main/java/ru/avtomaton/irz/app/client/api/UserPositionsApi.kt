package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.avtomaton.irz.app.client.api.users.models.PositionDto
import java.util.*

/**
 * @author Anton Akkuzin
 */
interface UserPositionsApi {

    @GET("/api/user_positions")
    suspend fun userPositions(@Query("userId") userId: UUID): Response<List<PositionDto>>

    @GET("/api/user_positions/my")
    suspend fun myUserPositions(): Response<List<PositionDto>>
}