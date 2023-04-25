package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class AuthorDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("patronymic") val patronymic: String,
    @SerializedName("imageId") val imageId: UUID?
)