package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import ru.avtomaton.irz.app.model.pojo.ImageDto

/**
 * @author Anton Akkuzin
 */
data class NewsBody(
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String,
    @SerializedName("isPublic") val isPublic: Boolean,
    @SerializedName("image") val image: ImageDto?,
)
