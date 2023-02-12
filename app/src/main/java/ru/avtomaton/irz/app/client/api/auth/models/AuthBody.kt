package ru.avtomaton.irz.app.client.api.auth.models

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class AuthBody(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
