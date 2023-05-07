package ru.avtomaton.irz.app.model.pojo

import java.util.*

/**
 * @author Anton Akkuzin
 */
data class Chat(
    val id: UUID,
    val recipient: UserShort,
    val lastMessage: Message?,
    val unreadedCount: Int
)
