package ru.avtomaton.irz.app.activity

import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.model.OpResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
open class AppCompatActivityBase : AppCompatActivity() {

    protected val threeDots = "…"
    protected val tag: String = this.javaClass.simpleName
    private val code = 1337
    protected lateinit var onImageUploaded: (Uri) -> Unit
    protected var imageUri: Uri? = null
    protected val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it == null) return@registerForActivityResult
        imageUri = it
        onImageUploaded(it)
    }

    fun Uri.toImageBytes(): ByteArray? {
        return contentResolver.openInputStream(this)?.use { uri -> uri.buffered().readBytes() }
    }

    companion object {
        val dateFormat: SimpleDateFormat =
            SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale("ru"))

        val dateFormatV2: SimpleDateFormat =
            SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    }

    fun async(block: suspend () -> Unit) {
        this.lifecycleScope.launch { block() }
    }

    fun error() {
        val onLoadErrorMessage: String = getString(R.string.common_on_load_error)
        Toast.makeText(this, onLoadErrorMessage, Toast.LENGTH_SHORT).show()
    }

    protected fun warn(value: String) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    }

    fun <T> OpResult<T>.letIfSuccess(block: T.() -> Unit) {
        if (this.isFailure) {
            error()
            return
        }
        block(this.value())
    }
}