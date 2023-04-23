package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.constants.DateFormats
import ru.avtomaton.irz.app.databinding.ActivityCurrentNewsItemBinding
import ru.avtomaton.irz.app.infra.SessionManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class CurrentNewsItemActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val news = intent.getSerializableExtra(key) as News // ignore, requires Api level 33
        val binding = ActivityCurrentNewsItemBinding.inflate(layoutInflater)
        binding.newsTitle.text = news.title
        authorImage?.also { binding.newsAuthorImage.setImageBitmap(it) }
        val name = "${news.author.surname} ${news.author.firstName}"
        binding.newsAuthorName.text = name
        binding.newsDatetime.text = DateFormats.simpleDateFormat.format(news.dateTime)
        binding.newsImage.setImageDrawable(null)
        image?.also { binding.newsImage.setImageBitmap(it) }
        binding.newsText.text = news.text
        if (SessionManager.isAuthenticated()) {
            binding.newsAuthor.setOnClickListener {
                onProfileClick(news.author.id)
            }
        }
        setContentView(binding.root)
    }

    private fun onProfileClick(id: UUID) {
        startActivity(ProfileActivity.openProfile(this, id))
    }
}