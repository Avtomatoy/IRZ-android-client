package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class MessageToSend(
    @SerializedName("userId") val userId: UUID,
    @SerializedName("text") val text: String?,
    @SerializedName("image") val image: ImageDto?
)
