package ru.avtomaton.irz.app.client.api.images

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.images.model.ImageDto
import ru.avtomaton.irz.app.infra.Base64Converter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Anton Akkuzin
 */
class ImageRepository {

    private val cache: MutableMap<UUID, Bitmap> = ConcurrentHashMap()
    suspend fun getImage(imageId: UUID): Bitmap? {
        return cache[imageId] ?: downloadImage(imageId)
    }

    private suspend fun downloadImage(imageId: UUID): Bitmap? {
        val imageResponse: Response<ImageDto>?
        try {
            imageResponse = IrzClient.imagesApi.getImage(imageId)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            return null
        }

        if (!imageResponse.isSuccessful) {
            return null
        }
        val data = imageResponse.body()!!.data
        try {
            val bitmap = Base64Converter.convert(data)!!
            cache[imageId] = bitmap
            return bitmap
        } catch (ignore: Throwable) {
        }
        return null
    }
}