package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class ImageDto(
    @SerializedName("name") val name: String,
    @SerializedName("extension") val extension: String,
    @SerializedName("data") val data: String
)
