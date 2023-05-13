package ru.avtomaton.irz.app.model.repository

import android.graphics.Bitmap
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.OpResult
import ru.avtomaton.irz.app.services.Base64Converter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Anton Akkuzin
 */
object ImageRepository : Repository() {

    private val cache: MutableMap<UUID, Bitmap> = ConcurrentHashMap()

    suspend fun getImage(imageId: UUID): OpResult<Bitmap> {
        val bitmap = cache[imageId]
        return if (bitmap != null) OpResult.Success(bitmap) else downloadImage(imageId)
    }

    private suspend fun downloadImage(imageId: UUID): OpResult<Bitmap> {
        return try {
            val response = IrzHttpClient.imagesApi.getImage(imageId)
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
            ex.printStackTrace()
            OpResult.Failure()
        }
    }
}