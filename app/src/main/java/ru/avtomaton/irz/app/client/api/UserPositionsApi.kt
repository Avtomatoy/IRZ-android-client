package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.avtomaton.irz.app.model.pojo.PositionDto
import ru.avtomaton.irz.app.constants.MY_POSITIONS
import ru.avtomaton.irz.app.constants.POSITIONS
import java.util.*

/**
 * @author Anton Akkuzin
 */
interface UserPositionsApi {

    @GET(POSITIONS)
    suspend fun userPositions(@Query("userId") userId: UUID): Response<List<PositionDto>>

    @GET(MY_POSITIONS)
    suspend fun myUserPositions(): Response<List<PositionDto>>
}