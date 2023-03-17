package ru.avtomaton.irz.app.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import ru.avtomaton.irz.app.R

/**
 * @author Anton Akkuzin
 */
class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        onBackPressedDispatcher.addCallback(BackPressedCallback())
    }

    inner class BackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // do nothing
        }
    }
}