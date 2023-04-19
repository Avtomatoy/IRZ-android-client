package ru.avtomaton.irz.app.client.api.users.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * @author Anton Akkuzin
 */
data class PositionDto(
    @SerializedName("start") val start: Date,
    @SerializedName("end") val end: Date,
    @SerializedName("position") val position: SubPositionDto
)

data class SubPositionDto(
    @SerializedName("name") val name: String
)