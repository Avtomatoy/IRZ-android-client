package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.avtomaton.irz.app.model.pojo.UserDto
import ru.avtomaton.irz.app.constants.USERS
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface UsersApi {

    @GET(USERS)
    suspend fun getUsers(
        @Query("PositionId") positionId: UUID?,
        @Query("Role") role: String?,
        @Query("SearchString") searchString: String?,
        @Query("PageIndex") pageIndex: Int,
        @Query("PageSize") pageSize: Int
    ): Response<List<UserDto>>

    @GET("$USERS/me")
    suspend fun getMe(): Response<UserDto>

    @GET("$USERS/{id}")
    suspend fun user(@Path("id") id: UUID): Response<UserDto>
}
