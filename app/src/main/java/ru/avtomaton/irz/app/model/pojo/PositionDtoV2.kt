package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class PositionDtoV2(
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String,
)