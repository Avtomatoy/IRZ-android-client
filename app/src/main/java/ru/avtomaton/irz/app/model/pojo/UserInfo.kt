package ru.avtomaton.irz.app.model.pojo

import com.google.gson.annotations.SerializedName

/**
 * @author Anton Akkuzin
 */
data class UserInfo(
    @SerializedName("aboutMyself") val aboutMyself: String,
    @SerializedName("myDoings") val myDoings: String,
    @SerializedName("skills") val skills: String,
)
