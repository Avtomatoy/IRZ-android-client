package ru.avtomaton.irz.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.activity.util.CommentsAdapter
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.ActivityCurrentNewsItemBinding
import ru.avtomaton.irz.app.model.pojo.CommentToSend
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class CurrentNewsItemActivity : AppCompatActivityBase() {

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
            news.author.imageId?.also {
                Glide.with(this@CurrentNewsItemActivity)
                    .loadImageBy(it)
                    .centerCrop()
                    .into(newsAuthorImage)
            }
            newsAuthorName.text = news.author.fullName
            newsDatetime.text = dateFormat.format(news.dateTime)
            newsImage.setImageDrawable(null)
            news.imageId?.also {
                Glide.with(this@CurrentNewsItemActivity)
                    .loadImageBy(it)
                    .into(newsImage)
            }
            newsText.text = news.text
            postCommentButton.setOnClickListener { post() }
            if (CredentialsManager.isAuthenticated()) {
                newsAuthor.setOnClickListener { onProfileClick(news.author.id) }
            }
            if (CredentialsManager.isAuthenticated()) {
                this@CurrentNewsItemActivity.adapter =
                    CommentsAdapter(this@CurrentNewsItemActivity, news.id)
                comments.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = this@CurrentNewsItemActivity.adapter
                }
            } else apply {
                commentsArea.visibility = View.GONE
                comments.visibility = View.GONE
            }
            setContentView(root)
        }
    }

    private fun post() {
        val input = binding.input.text.toString()
        if (input.isBlank()) return
        adapter.post(CommentToSend(newsId, input))
        binding.input.text = null
        val service = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
    }

    private fun onProfileClick(id: UUID) {
        startActivity(ProfileActivity.openProfile(this, id))
    }

    companion object {

        private const val key: String = "news_item"

        fun open(context: Context, news: News): Intent {
            val intent = Intent(context, CurrentNewsItemActivity::class.java)
            intent.putExtra(key, news)
            return intent
        }
    }
}