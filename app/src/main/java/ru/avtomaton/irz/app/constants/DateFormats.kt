package ru.avtomaton.irz.app.constants

import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Anton Akkuzin
 */
object DateFormats {
    val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru"))
}