package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.databinding.ActivityPostNewsBinding
import ru.avtomaton.irz.app.model.pojo.ImageDto
import ru.avtomaton.irz.app.model.pojo.NewsBody
import ru.avtomaton.irz.app.model.repository.NewsRepository
import ru.avtomaton.irz.app.model.repository.UserRepository
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        missingHeader = getString(R.string.post_news_missing_header)
        missingText = getString(R.string.post_news_missing_text)
        imageLoadError = getString(R.string.common_on_image_upload_error)
        errorOnPost = getString(R.string.post_news_error_on_post)

        binding = ActivityPostNewsBinding.inflate(layoutInflater)
        binding.postNewsButton.setOnClickListener { async { post() } }
        binding.removeImageButton.visibility = View.GONE
        binding.newsImage.visibility = View.GONE
        binding.addImageButton.setOnClickListener { uploadImage() }
        binding.removeImageButton.setOnClickListener { removeImage() }
        binding.publicNewsSwitch.visibility = View.GONE
        async { tryEnableSwitch() }

        onImageUploaded = { uri ->
            binding.newsImage.setImageURI(uri)
            binding.addImageButton.visibility = View.GONE
            binding.removeImageButton.visibility = View.VISIBLE
            binding.newsImage.visibility = View.VISIBLE
        }

        setContentView(binding.root)
    }

    private suspend fun tryEnableSwitch() {
        UserRepository.getMe().letIfSuccess {
            if (isSupport()) {
                binding.publicNewsSwitch.visibility = View.VISIBLE

            }
        }
    }

    private suspend fun post() {
        val title = binding.newsTitle.text.toString().trim()
        if (title.isBlank()) {
            warn(missingHeader)
            return
        }
        val text = binding.newsText.text.toString().trim()
        if (text.isBlank()) {
            warn(missingText)
            return
        }
        binding.newsImage.invalidate()
        var imageDto: ImageDto? = null
        if (binding.newsImage.drawable != null) {
            val base64 = Base64Converter.convert(binding.newsImage.drawable.toBitmap())
            if (base64.isFailure) {
                warn(imageLoadError)
                return
            }
            imageDto = ImageDto("image", "png", base64.value())
        }
        val newsBody = NewsBody(title, text, binding.publicNewsSwitch.isChecked, imageDto)
        warn("Минуточку…")
        if (!NewsRepository.postNews(newsBody)) {
            warn(errorOnPost)
            return
        }
        setResult(RESULT_OK)
        finish()
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