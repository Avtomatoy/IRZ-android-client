package ru.avtomaton.irz.app.client.api.news.models

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class NewsDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String,
    @SerializedName("imageId") val imageId: UUID?,
    @SerializedName("dateTime") val dateTime: Date,
    @SerializedName("isLiked") val isLiked: Boolean,
    @SerializedName("likesCount") val likesCount: Int,
    @SerializedName("author") val authorDto: AuthorDto,
    @SerializedName("commentCount") val commentCount: Int,
    @SerializedName("isClipped") val isClipped: Boolean,
    @SerializedName("isPublic") val isPublic: Boolean,
)
