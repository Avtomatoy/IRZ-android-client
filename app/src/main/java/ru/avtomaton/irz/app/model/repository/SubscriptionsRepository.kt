package ru.avtomaton.irz.app.model.repository

import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzHttpClient
import java.util.*

/**
 * @author Anton Akkuzin
 */
object SubscriptionsRepository : Repository() {

    suspend fun subscribe(userId: UUID): Boolean {
        return subscriptionAction { IrzHttpClient.subscriptionsApi.subscribe(userId) }
    }

    suspend fun unsubscribe(userId: UUID): Boolean {
        return subscriptionAction { IrzHttpClient.subscriptionsApi.unsubscribe(userId) }
    }

    private suspend fun subscriptionAction(block: suspend () -> Response<Unit>): Boolean {
        return tryForSuccess {
            block().isSuccessful
        }
    }
}