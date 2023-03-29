package ru.avtomaton.irz.app.client.api.likes

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface LikesApi {

    @POST("likes/like_news_entry")
    suspend fun like(@Query("newsEntryId") id: UUID): Response<Unit>

    @POST("likes/unlike_news_entry")
    suspend fun dislike(@Query("newsEntryId") id: UUID): Response<Unit>
}