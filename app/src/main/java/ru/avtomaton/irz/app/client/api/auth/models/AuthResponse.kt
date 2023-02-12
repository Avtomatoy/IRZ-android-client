package ru.avtomaton.irz.app.client.api.auth.models

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class AuthResponse(
    @SerializedName("jwt") val jwtToken : String,
    @SerializedName("refreshToken") val refreshToken: String
)
