package ru.avtomaton.irz.app.client.api.news

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.avtomaton.irz.app.client.api.news.models.NewsBody
import ru.avtomaton.irz.app.client.api.news.models.NewsDto
import ru.avtomaton.irz.app.client.news
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface NewsApi {

    @GET(news)
    suspend fun getNews(
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<NewsDto>>

    @GET("$news/{id}/full_text")
    suspend fun getNewsFullText(
        @Path("id") id: UUID
    ): Response<ResponseBody>

    @POST(news)
    suspend fun postNews(@Body newsBody: NewsBody): Response<Unit>
}