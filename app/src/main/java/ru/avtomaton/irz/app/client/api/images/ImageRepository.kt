package ru.avtomaton.irz.app.client.api.images

import android.graphics.Bitmap
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.Repository
import ru.avtomaton.irz.app.client.OpResult
import ru.avtomaton.irz.app.client.api.images.model.ImageDto
import ru.avtomaton.irz.app.infra.Base64Converter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Anton Akkuzin
 */
object ImageRepository : Repository() {

    private val cache: MutableMap<UUID, Bitmap> = ConcurrentHashMap()

    @Deprecated("")
    suspend fun getImageOld(imageId: UUID): Bitmap? {
        return cache[imageId] ?: downloadImageOld(imageId)
    }

    suspend fun getImage(imageId: UUID): OpResult<Bitmap> {
        val bitmap = cache[imageId]
        return if (bitmap != null) OpResult.Success(bitmap) else downloadImage(imageId)
    }

    private suspend fun downloadImage(imageId: UUID): OpResult<Bitmap> {
        return try {
            val response = IrzClient.imagesApi.getImage(imageId)
            if (!response.isSuccessful) {
                return OpResult.Failure()
            }
            val bitmapResult = Base64Converter.convert(response.body()!!.data)
            if (bitmapResult.isFailure) {
                return OpResult.Failure()
            }
            cache[imageId] = bitmapResult.value()
            bitmapResult
        } catch (ex: Throwable) {
            OpResult.Failure()
        }
    }

    @Deprecated("")
    private suspend fun downloadImageOld(imageId: UUID): Bitmap? {
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
            val bitmap = Base64Converter.convertOld(data)!!
            cache[imageId] = bitmap
            return bitmap
        } catch (ignore: Throwable) {
        }
        return null
    }
}