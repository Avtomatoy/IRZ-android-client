package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class CommentDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("text") val text: String,
    @SerializedName("dateTime") val dateTime: Date,
    @SerializedName("user") val user: UserShortDto,
)
