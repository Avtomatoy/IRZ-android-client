package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.images.model.ImageDto
import ru.avtomaton.irz.app.client.api.news.models.NewsBody
import ru.avtomaton.irz.app.client.api.users.UserRepository
import ru.avtomaton.irz.app.client.api.users.models.UserRoles
import ru.avtomaton.irz.app.databinding.ActivityPostNewsBinding
import ru.avtomaton.irz.app.infra.Base64Converter

/**
 * @author Anton Akkuzin
 */
class PostNewsActivity : AppCompatActivityBase() {

    private lateinit var binding: ActivityPostNewsBinding
    private val imageRequestCode: Int = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostNewsBinding.inflate(layoutInflater)
        binding.postNewsButton.setOnClickListener { post() }
        binding.removeImageButton.visibility = View.GONE
        binding.newsImage.visibility = View.GONE
        binding.addImageButton.setOnClickListener { uploadImage() }
        binding.removeImageButton.setOnClickListener { removeImage() }
        binding.publicNewsSwitch.visibility = View.GONE
        tryEnableSwitch()

        setContentView(binding.root)
    }

    private fun tryEnableSwitch() {
        this.lifecycleScope.launch {
            val meResult = UserRepository.getMe()
            if (meResult.isFailure) {
                onLoadErrorMessage()
                return@launch
            }
            if (UserRoles.isSupport(meResult.value())) {
                binding.publicNewsSwitch.visibility = View.VISIBLE
            }
        }
    }

    private fun post() {
        this.lifecycleScope.launch {
            val title = binding.newsTitle.text.toString().trim()
            if (title.isEmpty() || title.isBlank()) {
                Toast.makeText(this@PostNewsActivity, "Укажите заголовок!", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            val text = binding.newsText.text.toString().trim()
            if (text.isEmpty() || text.isBlank()) {
                Toast.makeText(this@PostNewsActivity, "Укажите текст новости!", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            binding.newsImage.invalidate()
            var imageDto: ImageDto? = null
            if (binding.newsImage.drawable != null) {
                val base64 = Base64Converter.convertOld(binding.newsImage.drawable.toBitmap())
                if (base64 == null) {
                    Toast.makeText(
                        this@PostNewsActivity,
                        "Проблема с загрузкой изображения...",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }
                imageDto = ImageDto("whatever", "png", base64)
            }
            val newsBody = NewsBody(title, text, binding.publicNewsSwitch.isChecked, imageDto)
            val response: Response<Unit>
            try {
                response = IrzClient.newsApi.postNews(newsBody)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                badPostToast()
                return@launch
            }
            if (!response.isSuccessful) {
                println(response.code())
                badPostToast()
                return@launch
            }
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun badPostToast() {
        Toast.makeText(
            this@PostNewsActivity,
            "Не удалось загрузить новость...",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, imageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || requestCode != imageRequestCode || data == null) {
            return
        }
        binding.newsImage.setImageURI(data.data)
        binding.addImageButton.visibility = View.GONE
        binding.removeImageButton.visibility = View.VISIBLE
        binding.newsImage.visibility = View.VISIBLE
    }

    private fun removeImage() {
        binding.newsImage.setImageURI(null)
        binding.addImageButton.visibility = View.VISIBLE
        binding.removeImageButton.visibility = View.GONE
        binding.newsImage.visibility = View.GONE
    }
}