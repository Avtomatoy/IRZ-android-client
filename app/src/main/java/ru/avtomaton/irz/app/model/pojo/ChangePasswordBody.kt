package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class ChangePasswordBody(
    @SerializedName("currentPassword") val currentPassword : String,
    @SerializedName("newPassword") val newPassword : String
)
