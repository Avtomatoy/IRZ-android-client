package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.Repository
import ru.avtomaton.irz.app.client.OpResult
import ru.avtomaton.irz.app.client.api.users.models.Position
import ru.avtomaton.irz.app.client.api.users.models.PositionDto
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object UserPositionsRepository : Repository() {

    suspend fun getMyPositions(): OpResult<List<Position>> {
        return getPositions { IrzClient.positionsApi.myUserPositions() }
    }

    suspend fun getUserPositions(id: UUID): OpResult<List<Position>> {
        return getPositions { IrzClient.positionsApi.userPositions(id) }
    }

    private suspend fun getPositions(
        request: suspend () -> Response<List<PositionDto>>
    ): OpResult<List<Position>> {
        return try {
            val response = request.invoke()
            if (response.isSuccessful) convert(response.body()!!) else OpResult.Failure()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }

    private fun convert(positionDtoList: List<PositionDto>): OpResult<List<Position>> {
        return OpResult.Success(positionDtoList
            .sortedByDescending { it.start }
            .map { Position(it.start, it.end, it.positionInfo.name) }
            .toList())
    }
}