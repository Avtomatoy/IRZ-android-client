package ru.avtomaton.irz.app.client.api.users.models

import com.google.gson.annotations.SerializedName
import java.util.Date
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
data class PositionDto(
    @SerializedName("start") val start: Date,
    @SerializedName("end") val end: Date,
    @SerializedName("position") val positionInfo: PositionInfo,
)

data class PositionInfo(
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String,
)