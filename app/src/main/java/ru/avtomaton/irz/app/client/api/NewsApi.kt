package ru.avtomaton.irz.app.client.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.avtomaton.irz.app.model.pojo.NewsBody
import ru.avtomaton.irz.app.model.pojo.NewsDto
import ru.avtomaton.irz.app.constants.NEWS
import ru.avtomaton.irz.app.constants.NEWS_COMMENTS
import ru.avtomaton.irz.app.model.pojo.CommentDto
import ru.avtomaton.irz.app.model.pojo.CommentToSend
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface NewsApi {

    @GET(NEWS)
    suspend fun getNews(
        @Query("AuthorId") authorId: UUID? = null,
        @Query("PublicOnly") public: Boolean = false,
        @Query("LikedOnly") liked: Boolean = false,
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<NewsDto>>

    @GET("$NEWS/{id}/full_text")
    suspend fun getNewsFullText(@Path("id") id: UUID): Response<ResponseBody>

    @POST(NEWS)
    suspend fun postNews(@Body newsBody: NewsBody): Response<Unit>

    @DELETE("$NEWS/{id}")
    suspend fun deleteNews(@Path("id") id: UUID): Response<Unit>

    @GET(NEWS_COMMENTS)
    suspend fun getComments(
        @Query("newsEntryId") newsId: UUID,
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<CommentDto>>

    @POST(NEWS_COMMENTS)
    suspend fun postComment(@Body comment: CommentToSend): Response<CommentDto>

    @DELETE("${NEWS_COMMENTS}/{id}")
    suspend fun deleteComment(@Path("id") id: UUID): Response<Unit>
}
