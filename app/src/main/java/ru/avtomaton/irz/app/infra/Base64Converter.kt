package ru.avtomaton.irz.app.infra

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * @author Anton Akkuzin
 */
object Base64Converter {

    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val encoder: Base64.Encoder = Base64.getEncoder()

    fun convert(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        return try {
            encoder.encodeToString(outputStream.toByteArray())
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }

    fun convert(base64Str: String): Bitmap? {
        return try {
            val bytes = decoder.decode(base64Str)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }
}