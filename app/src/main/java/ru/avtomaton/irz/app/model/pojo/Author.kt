package ru.avtomaton.irz.app.model.pojo

import android.graphics.Bitmap
import java.util.UUID
import java.io.Serializable

/**
 * @author Anton Akkuzin
 */
data class Author(
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    @Transient val image: Bitmap?
): Serializable