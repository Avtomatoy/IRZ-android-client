package ru.avtomaton.irz.app.client.api.users.models

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class MeResponse(
    @SerializedName("id") val id : String?,
    @SerializedName("firstName") val firstName : String?,
    @SerializedName("surname") val surname : String?,
    @SerializedName("patronymic") val patronymic : String?,
    @SerializedName("birthday") val birthday : String?,
    @SerializedName("image") val image : String?,
    @SerializedName("aboutMyself") val aboutMyself : String?,
    @SerializedName("myDoings") val myDoings : String?,
    @SerializedName("skills") val skills : String?
)
