package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.avtomaton.irz.app.constants.POSITIONS
import ru.avtomaton.irz.app.model.pojo.PositionDtoV2

/**
 * @author Anton Akkuzin
 */
interface PositionsApi {

    @GET(POSITIONS)
    suspend fun getPositions(@Query("PageIndex") pageIndex: Int, @Query("PageSize") pageSize: Int):
            Response<List<PositionDtoV2>>
}