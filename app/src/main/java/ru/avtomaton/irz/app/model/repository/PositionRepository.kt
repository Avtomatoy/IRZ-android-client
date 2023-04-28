package ru.avtomaton.irz.app.model.repository

import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.PositionInfo
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object PositionRepository {

    suspend fun getPositions(): OpResult<HashMap<String, UUID>> {
        return try {
            val response = IrzClient.positionsApi.getPositions(0, 100)
            if (!response.isSuccessful) {
                return OpResult.Failure()
            }
            val map = HashMap<String, UUID>()
            response.body()!!.map { PositionInfo(it.id, it.name) }
                .forEach { map[it.name] = it.id }
            OpResult.Success(map)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }
}