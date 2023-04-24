package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.databinding.NewsItemBinding
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsFeedAdapter(private val listener: NewsFeedAdapterListener) :
    RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder>() {

    private val news: MutableList<News> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(from, parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindNews(news[position])
        holder.itemView.setOnClickListener { listener.onNewsClick(holder.getNews()) }
        listener.onNewsUpdate(itemCount, position)
    }

    fun updateNews(news: List<News>) {
        news.forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    fun deleteNews(news: News) {
        val index = this.news.indexOf(news)
        if (this.news.remove(news)) {
            notifyItemRemoved(index)
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
            newsItem.newsTitle.text = news.title
            news.author.image?.also { newsItem.newsAuthorImage.setImageBitmap(it) }
            val name = "${news.author.surname} ${news.author.firstName}"
            newsItem.newsAuthorName.text = name
            newsItem.newsDatetime.text = AppCompatActivityBase.dateFormat.format(news.dateTime)
            newsItem.newsImage.setImageDrawable(null)
            news.image?.also { newsItem.newsImage.setImageBitmap(it) }
            newsItem.newsText.text = news.text
            newsItem.deleteButton.visibility = View.GONE
            if (!CredentialsManager.isAuthenticated()) {
                newsItem.likes.visibility = View.GONE
                return
            }
            newsItem.likes.setOnClickListener { like() }
            newsItem.newsAuthor.setOnClickListener { listener.onProfileClick(news.author.id) }
            if (news.canDelete) {
                newsItem.deleteButton.visibility = View.VISIBLE
                newsItem.deleteButton.setOnClickListener { listener.onNewsDelete(news) }
            }
        }

        private fun setLikeLogo() {
            val id = if (liked)
                R.drawable.heartred
            else
                R.drawable.heart
            newsItem.likeLogo.setImageResource(id)
        }

        private fun like() {
            liked = !liked
            likesCount += if (liked) 1 else -1
            listener.onNewsLike(news.id, liked)
            setLikeLogo()
            newsItem.likesCount.text = likesCount.toString()
        }
    }

    interface NewsFeedAdapterListener {
        fun onNewsUpdate(listSize: Int, position: Int)
        fun onNewsLike(newsId: UUID, liked: Boolean)
        fun onNewsClick(news: News)
        fun onNewsDelete(news: News)
        fun onProfileClick(id: UUID)
    }
}
