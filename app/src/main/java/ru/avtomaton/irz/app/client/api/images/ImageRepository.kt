package ru.avtomaton.irz.app.client.api.images

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.avtomaton.irz.app.client.IrzClient
import java.util.*

/**
 * @author Anton Akkuzin
 */
class ImageRepository {

    private val decoder: Base64.Decoder = Base64.getDecoder()

    suspend fun getImage(imageId: UUID): Bitmap? {
        val imageResponse = IrzClient.imagesApi.getImage(imageId)
        if (!imageResponse.isSuccessful) {
            return null
        }
        val data = imageResponse.body()!!.data
        try {
            return convertBase64Bytes(data)
        } catch (ignore: Throwable) {
        }

        return null
    }

    private fun convertBase64Bytes(base64Str: String): Bitmap {
        val bytes = decoder.decode(base64Str)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}