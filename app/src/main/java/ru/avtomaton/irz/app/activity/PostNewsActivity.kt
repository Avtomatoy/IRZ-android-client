package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.databinding.ActivityPostNewsBinding
import ru.avtomaton.irz.app.model.repository.NewsRepository
import ru.avtomaton.irz.app.model.repository.UserRepository

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

        binding = ActivityPostNewsBinding.inflate(layoutInflater).apply {
            postNewsButton.setOnClickListener { async { post() } }
            removeImageButton.visibility = View.GONE
            newsImage.visibility = View.GONE
            addImageButton.setOnClickListener { contract.launch("image/*") }
            removeImageButton.setOnClickListener {
                imageUri = null
                newsImage.setImageURI(null)
                addImageButton.visibility = View.VISIBLE
                removeImageButton.visibility = View.GONE
                newsImage.visibility = View.GONE
            }
            publicNewsSwitch.visibility = View.GONE
            async { tryEnableSwitch() }
            onImageUploaded = {
                Glide.with(this@PostNewsActivity).load(it).into(newsImage)
                addImageButton.visibility = View.GONE
                removeImageButton.visibility = View.VISIBLE
                newsImage.visibility = View.VISIBLE
            }
            setContentView(root)
        }
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
        val isPublic = binding.publicNewsSwitch.isChecked
        binding.newsImage.invalidate()
        val image = imageUri?.toImageBytes()
        warn("Минуточку…")
        if (!NewsRepository.postNews(title, text, isPublic, image)) {
            warn(errorOnPost)
            return
        }
        setResult(RESULT_OK)
        finish()
    }

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, PostNewsActivity::class.java)
        }
    }
}