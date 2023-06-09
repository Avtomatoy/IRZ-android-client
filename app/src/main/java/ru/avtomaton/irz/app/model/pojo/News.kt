package ru.avtomaton.irz.app.model.pojo

import android.graphics.Bitmap
import java.io.Serializable
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class News(
    val id: UUID,
    val title: String,
    val text: String,
    val imageId: UUID?,
    val dateTime: Date,
    val isLiked: Boolean,
    val likesCount: Int,
    val author: UserShort,
    val commentCount: Int,
    val canDelete: Boolean
): Serializable
