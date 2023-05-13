package ru.avtomaton.irz.app.model.repository

import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.Position
import ru.avtomaton.irz.app.model.pojo.PositionDto
import java.util.*

/**
 * @author Anton Akkuzin
 */
object UserPositionsRepository : Repository() {

    suspend fun getMyPositions(): OpResult<List<Position>> {
        return getPositions { IrzHttpClient.userPositionsApi.myUserPositions() }
    }

    suspend fun getUserPositions(id: UUID): OpResult<List<Position>> {
        return getPositions { IrzHttpClient.userPositionsApi.userPositions(id) }
    }

    private suspend fun getPositions(
        request: suspend () -> Response<List<PositionDto>>
    ): OpResult<List<Position>> {
        return tryForResult { request().letIfSuccess { convert(this.body()!!) } }
    }

    private fun convert(positionDtoList: List<PositionDto>): List<Position> {
        return positionDtoList
            .sortedByDescending { it.start }
            .map { Position(it.start, it.end, it.positionInfo.name) }
            .toList()
    }
}