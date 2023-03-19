package ru.avtomaton.irz.app.client.api.news.models

import android.graphics.Bitmap
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class News(
    val id: UUID,
    val title: String,
    val text: String,
    val image: Bitmap?,
    val dateTime: Date,
    val isLiked: Boolean,
    val likesCount: Int,
    val author: Author,
    val commentCount: Int
)
