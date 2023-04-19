package ru.avtomaton.irz.app.client.api.users.models

import android.graphics.Bitmap
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class User(
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String,
    val birthday: Date,
    val image: Bitmap?,
    val aboutMyself: String,
    val myDoings: String,
    val skills: String,
    val subscribersCount: Int,
    val subscriptionsCount: Int,
    val isSubscription: Boolean,
    val email: String,
    val isActiveAccount: Boolean,
    val roles: List<String>,
    val positions: List<Position>
)
