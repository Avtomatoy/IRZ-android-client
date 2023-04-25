package ru.avtomaton.irz.app.client.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.avtomaton.irz.app.model.pojo.ImageDto
import ru.avtomaton.irz.app.constants.IMAGES
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface ImagesApi {

    @GET("$IMAGES/{id}")
    suspend fun getImage(@Path("id") id: UUID): Response<ImageDto>
}
