package ru.avtomaton.irz.app.model.pojo

import java.util.*

/**
 * @author Anton Akkuzin
 */
data class Message(
    val id: UUID,
    val text: String,
    val imageId: UUID?,
    val date: Date,
    val senderId: UUID
)