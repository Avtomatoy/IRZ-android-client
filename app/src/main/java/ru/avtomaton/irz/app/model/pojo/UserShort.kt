package ru.avtomaton.irz.app.model.pojo

import android.graphics.Bitmap
import java.util.UUID
import java.io.Serializable

/**
 * @author Anton Akkuzin
 */
data class UserShort(
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val fullName: String,
    @Transient val image: Bitmap?
): Serializable