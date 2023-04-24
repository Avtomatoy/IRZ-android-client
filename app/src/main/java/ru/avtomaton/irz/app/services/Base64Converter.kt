package ru.avtomaton.irz.app.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import ru.avtomaton.irz.app.model.OpResult
import java.io.ByteArrayOutputStream
import java.util.*


/**
 * @author Anton Akkuzin
 */
object Base64Converter {

    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val encoder: Base64.Encoder = Base64.getEncoder()

    fun convert(bitmap: Bitmap): OpResult<String> {
        val outputStream = ByteArrayOutputStream()
        return try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            OpResult.Success(encoder.encodeToString(outputStream.toByteArray()))
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }

    fun convert(base64Str: String): OpResult<Bitmap> {
        return try {
            val bytes = decoder.decode(base64Str)
            val value = BitmapFactory.decodeByteArray(decoder.decode(base64Str), 0, bytes.size)
            OpResult.Success(value)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            OpResult.Failure()
        }
    }
}