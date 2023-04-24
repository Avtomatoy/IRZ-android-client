package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.databinding.ActivityCurrentNewsItemBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val news = intent.getSerializableExtra(key) as News // ignore, requires Api level 33
        val binding = ActivityCurrentNewsItemBinding.inflate(layoutInflater)
        binding.newsTitle.text = news.title
        authorImage?.also { binding.newsAuthorImage.setImageBitmap(it) }
        val name = "${news.author.surname} ${news.author.firstName}"
        binding.newsAuthorName.text = name
        binding.newsDatetime.text = dateFormat.format(news.dateTime)
        binding.newsImage.setImageDrawable(null)
        image?.also { binding.newsImage.setImageBitmap(it) }
        binding.newsText.text = news.text
        if (CredentialsManager.isAuthenticated()) {
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