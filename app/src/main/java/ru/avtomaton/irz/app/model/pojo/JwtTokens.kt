package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class JwtTokens(
    @SerializedName("jwt") val jwtToken : String,
    @SerializedName("refreshToken") val refreshToken: String
)
