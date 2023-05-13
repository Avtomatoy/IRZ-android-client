package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class PositionDto(
    @SerializedName("start") val start: Date,
    @SerializedName("end") val end: Date,
    @SerializedName("position") val positionInfo: PositionInfo,
)
