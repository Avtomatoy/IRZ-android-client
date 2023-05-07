package ru.avtomaton.irz.app.model.pojo

import android.graphics.Bitmap
import java.util.*

/**
 * @author Anton Akkuzin
 */
data class Message(
    val id: UUID,
    val text: String,
    val image: Bitmap?,
    val date: Date,
    val senderId: UUID
)