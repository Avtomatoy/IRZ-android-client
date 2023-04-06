package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.databinding.NewsItemBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsFeedAdapter(private val listener: NewsFeedAdapterListener) :
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
        holder.itemView.setOnClickListener { listener.onNewsClick(holder.getNews()) }
        listener.onUpdate(itemCount, position)
    }

    fun updateNews(news: MutableList<News>) {
        news.forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    inner class NewsViewHolder(
        private val newsItem: NewsItemBinding
    ) :
        RecyclerView.ViewHolder(newsItem.root) {

        private var liked: Boolean = false
        private var likesCount: Int = 0
        private lateinit var news: News

        fun getNews(): News = news

        fun bindNews(news: News) {
            this.news = news
            liked = news.isLiked
            likesCount = news.likesCount
            setLikeLogo()
            newsItem.likesCount.text = news.likesCount.toString()
            newsItem.commentsCount.text = news.commentCount.toString()
            newsItem.likes.setOnClickListener { like() }
            newsItem.newsTitle.text = news.title
            news.author.image?.also { newsItem.newsAuthorImage.setImageBitmap(it) }
            val name = "${news.author.surname} ${news.author.firstName}"
            newsItem.newsAuthorName.text = name
            newsItem.newsDatetime.text = dateFormat.format(news.dateTime)
            newsItem.newsImage.setImageDrawable(null)
            news.image?.also { newsItem.newsImage.setImageBitmap(it) }
            newsItem.newsText.text = news.text
        }

        private fun setLikeLogo() {
            val id = if (liked) R.drawable.heartred else R.drawable.heart
            newsItem.likeLogo.setImageResource(id)
        }

        private fun like() {
            liked = !liked
            if (liked) {
                listener.onLike(news.id)
                likesCount++
            } else {
                listener.onDislike(news.id)
                likesCount--
            }
            setLikeLogo()
            newsItem.likesCount.text = likesCount.toString()
        }
    }

    interface NewsFeedAdapterListener {
        fun onUpdate(listSize: Int, position: Int)
        fun onLike(newsId: UUID)

        fun onDislike(newsId: UUID)

        fun onNewsClick(news: News)
    }
}
