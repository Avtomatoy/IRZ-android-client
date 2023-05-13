package ru.avtomaton.irz.app.model.repository

import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.model.pojo.PositionInfo
import java.util.*

/**
 * @author Anton Akkuzin
 */
object PositionRepository : Repository() {

    suspend fun getPositions(): OpResult<HashMap<String, UUID>> {
        return tryForResult {
            IrzHttpClient.positionsApi.getPositions(0, 100).letIfSuccess {
                val map = HashMap<String, UUID>()
                this.body()!!.map { PositionInfo(it.id, it.name) }.forEach { map[it.name] = it.id }
                map
            }
        }
    }
}