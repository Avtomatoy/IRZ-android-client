package ru.avtomaton.irz.app.client.api.auth.models

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class ChangePasswordBody(
    @SerializedName("currentPassword") val currentPassword : String,
    @SerializedName("newPassword") val newPassword : String
)
