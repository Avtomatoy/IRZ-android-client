package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class ChatDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("recipient") val recipient: UserShortDto,
    @SerializedName("lastMessage") val lastMessage: MessageDto?,
    @SerializedName("unreadedCount") val unreadedCount: Int
)
