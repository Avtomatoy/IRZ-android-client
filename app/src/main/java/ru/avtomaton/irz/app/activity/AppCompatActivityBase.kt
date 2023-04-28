package ru.avtomaton.irz.app.activity

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.model.OpResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
open class AppCompatActivityBase : AppCompatActivity() {

    val tag: String = this.javaClass.simpleName

    companion object {
        val dateFormat: SimpleDateFormat =
            SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru"))
    }

    protected fun error() {
        val onLoadErrorMessage: String = getString(R.string.common_on_load_error)
        Toast.makeText(this, onLoadErrorMessage, Toast.LENGTH_SHORT).show()
    }

    protected fun warn(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    protected fun <T> OpResult<T>.applyIfSuccess(block: T.() -> Unit) {
        if (this.isFailure) {
            error()
            return
        }
        block(this.value())
    }
}