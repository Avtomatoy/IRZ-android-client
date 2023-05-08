package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class CommentToSend(
    @SerializedName("newsEntryId") val newsId: UUID,
    @SerializedName("text") val text: String,
)
