package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

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
    @SerializedName("author") val authorDto: UserShortDto,
    @SerializedName("commentCount") val commentCount: Int,
    @SerializedName("isClipped") val isClipped: Boolean,
    @SerializedName("isPublic") val isPublic: Boolean,
)
