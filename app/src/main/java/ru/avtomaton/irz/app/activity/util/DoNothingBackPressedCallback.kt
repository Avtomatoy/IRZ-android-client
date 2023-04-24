package ru.avtomaton.irz.app.activity.util

import androidx.activity.OnBackPressedCallback

/**
 * @author Anton Akkuzin
 */
class DoNothingBackPressedCallback: OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {}
}