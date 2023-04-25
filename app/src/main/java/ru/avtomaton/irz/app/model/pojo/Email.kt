package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class Email(
    @SerializedName("email") val email: String,
)
