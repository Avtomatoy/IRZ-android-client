package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
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

    protected val threeDots: String = "…"
    protected val tag: String = this.javaClass.simpleName
    private val code: Int = 1488
    protected lateinit var onImageUploaded: (Uri) -> Unit

    companion object {
        val dateFormat: SimpleDateFormat =
            SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale("ru"))

        val dateFormatV2: SimpleDateFormat =
            SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    }

    protected fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        @Suppress("DEPRECATION")
        startActivityForResult(intent, code)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || requestCode != code || data == null) {
            return
        }
        onImageUploaded(data.data!!)
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

    protected fun String.orEmpty(): String =
        if (this.isEmpty() || this.isBlank()) threeDots else this

    protected fun String.emptyBlank(): Boolean = this.isEmpty() || this.isBlank()
}