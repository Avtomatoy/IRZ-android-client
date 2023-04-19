package ru.avtomaton.irz.app.client.api.users.models

import java.util.Date

/**
 * @author Anton Akkuzin
 */
data class Position(
    val start: Date,
    val end: Date?,
    val name: String,
)