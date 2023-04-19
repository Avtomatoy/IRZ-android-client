package ru.avtomaton.irz.app.client.api.images

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.avtomaton.irz.app.client.api.images.model.ImageDto
import ru.avtomaton.irz.app.client.images
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
interface ImagesApi {

    @GET("$images/{id}")
    suspend fun getImage(@Path("id") id: UUID) : Response<ImageDto>
}