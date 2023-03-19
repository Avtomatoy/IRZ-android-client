package ru.avtomaton.irz.app.client.api.news

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import ru.avtomaton.irz.app.client.api.news.models.NewsDto
import ru.avtomaton.irz.app.client.news

/**
 * @author Anton Akkuzin
 */
interface NewsApi {

    @GET(news)
    suspend fun getNews(
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<NewsDto>>
}