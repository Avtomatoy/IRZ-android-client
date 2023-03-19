package ru.avtomaton.irz.app.activity.news.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.databinding.NewsItemBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsFeedAdapter(private val updater: Updater) :
    RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder>() {

    private val news: MutableList<News> = mutableListOf()
    private val dateFormat: SimpleDateFormat =
        SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(from, parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindNews(news[position])
        updater.onUpdate(itemCount, position)
    }

    fun updateNews(news: MutableList<News>) {
        news.forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    inner class NewsViewHolder(private val newsItem: NewsItemBinding) :
        RecyclerView.ViewHolder(newsItem.root) {

        fun bindNews(news: News) {
            newsItem.newsTitle.text = news.title
            news.author.image?.also { newsItem.newsAuthorImage.setImageBitmap(it) }
            val name = "${news.author.surname} ${news.author.firstName}"
            newsItem.newsAuthorName.text = name
            newsItem.newsDatetime.text = dateFormat.format(news.dateTime)
            newsItem.newsImage.setImageDrawable(null)
            news.image?.also { newsItem.newsImage.setImageBitmap(it) }
            newsItem.newsText.text = news.text
        }
    }

    @FunctionalInterface
    interface Updater {
        fun onUpdate(listSize: Int, position: Int)
    }
}
