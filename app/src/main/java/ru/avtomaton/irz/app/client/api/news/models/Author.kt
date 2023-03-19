package ru.avtomaton.irz.app.client.api.news.models

import android.graphics.Bitmap
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class Author(
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String,
    val image: Bitmap?
)