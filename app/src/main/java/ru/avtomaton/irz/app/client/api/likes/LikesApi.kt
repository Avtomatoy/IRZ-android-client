package ru.avtomaton.irz.app.client.api.likes

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import ru.avtomaton.irz.app.constants.LIKES_LIKE
import ru.avtomaton.irz.app.constants.LIKES_UNLIKE
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface LikesApi {

    @POST(LIKES_LIKE)
    suspend fun like(@Query("newsEntryId") id: UUID): Response<Unit>

    @POST(LIKES_UNLIKE)
    suspend fun dislike(@Query("newsEntryId") id: UUID): Response<Unit>
}