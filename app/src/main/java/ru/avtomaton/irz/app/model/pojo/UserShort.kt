package ru.avtomaton.irz.app.model.pojo

import android.graphics.Bitmap
import java.io.Serializable
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class UserShort(
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val fullName: String,
    val imageId: UUID?
//    @Transient val image: Bitmap?
): Serializable