package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.model.pojo.ImageDto
import ru.avtomaton.irz.app.model.pojo.NewsBody
import ru.avtomaton.irz.app.model.repository.UserRepository
import ru.avtomaton.irz.app.databinding.ActivityPostNewsBinding
import ru.avtomaton.irz.app.services.Base64Converter

/**
 * @author Anton Akkuzin
 */
class PostNewsActivity : AppCompatActivityBase() {

    private lateinit var binding: ActivityPostNewsBinding
    private lateinit var missingHeader: String
    private lateinit var missingText: String
    private lateinit var imageLoadError: String
    private lateinit var errorOnPost: String
    private val imageRequestCode: Int = 1337

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        missingHeader = getString(R.string.post_news_missing_header)
        missingText = getString(R.string.post_news_missing_text)
        imageLoadError = getString(R.string.post_news_image_load_error)
        errorOnPost = getString(R.string.post_news_error_on_post)

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
                error()
                return@launch
            }
            if (meResult.value().isSupport()) {
                binding.publicNewsSwitch.visibility = View.VISIBLE
            }
        }
    }

    private fun post() {
        this.lifecycleScope.launch {
            val title = binding.newsTitle.text.toString().trim()
            if (title.isEmpty() || title.isBlank()) {
                warn(missingHeader)
                return@launch
            }
            val text = binding.newsText.text.toString().trim()
            if (text.isEmpty() || text.isBlank()) {
                warn(missingText)
                return@launch
            }
            binding.newsImage.invalidate()
            var imageDto: ImageDto? = null
            if (binding.newsImage.drawable != null) {
                val base64 = Base64Converter.convert(binding.newsImage.drawable.toBitmap())
                if (base64.isFailure) {
                    warn(imageLoadError)
                    return@launch
                }
                imageDto = ImageDto("image", "png", base64.value())
            }
            val newsBody = NewsBody(title, text, binding.publicNewsSwitch.isChecked, imageDto)
            val response: Response<Unit>
            try {
                response = IrzClient.newsApi.postNews(newsBody)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                warn(errorOnPost)
                return@launch
            }
            if (!response.isSuccessful) {
                println(response.code())
                warn(errorOnPost)
                return@launch
            }
            setResult(RESULT_OK)
            finish()
        }
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

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, PostNewsActivity::class.java)
        }
    }
}