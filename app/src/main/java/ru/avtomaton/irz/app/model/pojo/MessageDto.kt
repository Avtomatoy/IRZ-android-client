package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class MessageDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("text") val text: String?,
    @SerializedName("imageId") val imageId: UUID?,
    @SerializedName("dateTime") val date: Date,
    @SerializedName("senderId") val senderId: UUID
)
