package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.databinding.ActivityCurrentNewsItemBinding
import ru.avtomaton.irz.app.infra.SessionManager
import ru.avtomaton.irz.app.infra.UserManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
class CurrentNewsItemActivity: AppCompatActivity() {

    // todo: probably there's a better solution
    // workaround to pass potentially big images to activity
    companion object {
        var image: Bitmap? = null
        var authorImage: Bitmap? = null
    }

    private val dateFormat: SimpleDateFormat =
        SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val news = intent.getSerializableExtra(newsItemKey) as News // ignore, requires Api level 33
        val binding = ActivityCurrentNewsItemBinding.inflate(layoutInflater)
        binding.newsTitle.text = news.title
        authorImage?.also { binding.newsAuthorImage.setImageBitmap(it) }
        val name = "${news.author.surname} ${news.author.firstName}"
        binding.newsAuthorName.text = name
        binding.newsDatetime.text = dateFormat.format(news.dateTime)
        binding.newsImage.setImageDrawable(null)
        image?.also { binding.newsImage.setImageBitmap(it) }
        binding.newsText.text = news.text
        if (SessionManager.authenticated()) {
            binding.newsAuthor.setOnClickListener {
                val id: UUID? =
                    if (UserManager.getInfo()!!.id != news.author.id) news.author.id
                    else null
                onProfileClick(id)
            }
        }
        setContentView(binding.root)
    }

    private fun onProfileClick(id: UUID?) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("id", id?.toString() ?: "")
        startActivity(intent)
    }
}