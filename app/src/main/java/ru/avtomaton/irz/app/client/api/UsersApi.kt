package ru.avtomaton.irz.app.client.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.avtomaton.irz.app.constants.USERS
import ru.avtomaton.irz.app.model.pojo.ImageDto
import ru.avtomaton.irz.app.model.pojo.UserDto
import ru.avtomaton.irz.app.model.pojo.UserInfo
import java.util.*

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

    @PUT("$USERS/me/update_photo")
    @Multipart
    suspend fun updatePhoto(@Part image: MultipartBody.Part): Response<Unit>

    @PUT("$USERS/me/delete_photo")
    suspend fun deletePhoto(): Response<Unit>

    @PUT("$USERS/me/update_info")
    suspend fun updateInfo(@Body userInfo: UserInfo): Response<Unit>

    @GET("$USERS/{id}")
    suspend fun user(@Path("id") id: UUID): Response<UserDto>
}
