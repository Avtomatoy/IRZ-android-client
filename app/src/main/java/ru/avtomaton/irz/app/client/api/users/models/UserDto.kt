package ru.avtomaton.irz.app.client.api.users.models

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class UserDto(
    @SerializedName("id") val id: UUID,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("surname") val surname: String,
    @SerializedName("patronymic") val patronymic: String?,
    @SerializedName("birthday") val birthday: Date,
    @SerializedName("imageId") val imageId: UUID?,
    @SerializedName("aboutMyself") val aboutMyself: String?,
    @SerializedName("myDoings") val myDoings: String?,
    @SerializedName("skills") val skills: String?,
    @SerializedName("subscribersCount") val subscribersCount: Int,
    @SerializedName("subscriptionsCount") val subscriptionsCount: Int,
    @SerializedName("isSubscription") val isSubscription: Boolean,
    @SerializedName("email") val email: String,
    @SerializedName("isActiveAccount") val isActiveAccount: Boolean,
    // TODO: roles
    // TODO: positions
)
