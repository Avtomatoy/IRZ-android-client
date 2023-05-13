package ru.avtomaton.irz.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.avtomaton.irz.app.activity.util.CommentsAdapter
import ru.avtomaton.irz.app.databinding.ActivityCurrentNewsItemBinding
import ru.avtomaton.irz.app.model.pojo.CommentToSend
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class CurrentNewsItemActivity : AppCompatActivityBase() {

    // todo: probably there's a better solution
    // workaround to pass potentially big images to activity
    companion object {
        private const val key: String = "news_item"
        var image: Bitmap? = null
        var authorImage: Bitmap? = null

        fun open(context: Context, news: News): Intent {
            val intent = Intent(context, CurrentNewsItemActivity::class.java)
            intent.putExtra(key, news)
            image = news.image
            authorImage = news.author.image

            return intent
        }
    }

    private lateinit var binding: ActivityCurrentNewsItemBinding
    private lateinit var newsId: UUID
    private lateinit var adapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        val news = intent.getSerializableExtra(key) as News
        newsId = news.id
        binding = ActivityCurrentNewsItemBinding.inflate(layoutInflater).apply {
            newsTitle.text = news.title
            authorImage?.also { newsAuthorImage.setImageBitmap(it) }
            @SuppressLint("SetTextI18n")
            newsAuthorName.text = "${news.author.surname} ${news.author.firstName}"
            newsDatetime.text = dateFormat.format(news.dateTime)
            newsImage.setImageDrawable(null)
            image?.also { newsImage.setImageBitmap(it) }
            newsText.text = news.text
            postCommentButton.setOnClickListener { post() }
            if (CredentialsManager.isAuthenticated()) {
                newsAuthor.setOnClickListener { onProfileClick(news.author.id) }
            }
        }
        if (CredentialsManager.isAuthenticated()) {
            adapter = CommentsAdapter(this, news.id)
            binding.comments.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = this@CurrentNewsItemActivity.adapter
            }
        } else {
            binding.apply {
                commentsArea.visibility = View.GONE
                comments.visibility = View.GONE
            }
        }
        setContentView(binding.root)
    }

    private fun post() {
        val input = binding.input.text.toString()
        if (input.isBlank()) {
            return
        }
        adapter.post(CommentToSend(newsId, input))
        binding.input.text = null
        val service = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    private fun onProfileClick(id: UUID) {
        startActivity(ProfileActivity.openProfile(this, id))
    }
}