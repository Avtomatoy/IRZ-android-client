package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import ru.avtomaton.irz.app.constants.SUBSCRIPTIONS_SUBSCRIBE
import ru.avtomaton.irz.app.constants.SUBSCRIPTIONS_UNSUBSCRIBE
import java.util.*

/**
 * @author Anton Akkuzin
 */
interface SubscriptionsApi {

    @POST(SUBSCRIPTIONS_SUBSCRIBE)
    suspend fun subscribe(@Query("userId") userId: UUID): Response<Unit>

    @POST(SUBSCRIPTIONS_UNSUBSCRIBE)
    suspend fun unsubscribe(@Query("userId") userId: UUID): Response<Unit>
}
