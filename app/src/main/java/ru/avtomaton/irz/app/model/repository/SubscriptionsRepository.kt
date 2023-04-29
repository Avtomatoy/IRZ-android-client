package ru.avtomaton.irz.app.model.repository

import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
object SubscriptionsRepository {

    suspend fun subscribe(userId: UUID): Boolean {
        return subscriptionAction { IrzClient.subscriptionsApi.subscribe(userId) }
    }

    suspend fun unsubscribe(userId: UUID): Boolean {
        return subscriptionAction { IrzClient.subscriptionsApi.unsubscribe(userId) }
    }

    private suspend fun subscriptionAction(block: suspend () -> Response<Unit>): Boolean {
        return try {
            block().isSuccessful
        } catch (ex: Throwable) {
            ex.printStackTrace()
            false
        }
    }
}