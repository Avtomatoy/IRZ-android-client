package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.activity.CurrentNewsItemActivity
import ru.avtomaton.irz.app.activity.ProfileActivity
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.databinding.NewsItemBinding
import ru.avtomaton.irz.app.model.repository.NewsRepository
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsFeedAdapter(
    private val context: AppCompatActivityBase,
    private val userId: UUID? = null
) :
    RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder>() {

    private val news: MutableList<News> = mutableListOf()
    private val pageSize = 10

    init {
        context.async { updateNews(0, pageSize) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = NewsItemBinding.inflate(from, parent, false)
        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bindNews(news[position])
        holder.itemView.setOnClickListener { context.async { onNewsClick(holder.getNews()) } }
        onNewsUpdate(itemCount, position)
    }

    fun updateNews(news: List<News>) {
        news.forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    private suspend fun updateNews(pageIndex: Int, pageSize: Int) {
        val result = if (userId == null)
            NewsRepository.getNews(pageIndex, pageSize)
        else
            NewsRepository.getAuthorNews(userId, pageIndex, pageSize)
        if (result.isFailure) {
            context.error()
            return
        }
        result.value().forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    private fun onNewsUpdate(listSize: Int, position: Int) {
        if (listSize % pageSize != 0 || listSize - position != 5)
            return
        context.async { updateNews(listSize / pageSize, pageSize) }
    }

    private suspend fun onNewsClick(news: News) {
        if (!CredentialsManager.isAuthenticated()) {
            return
        }
        val result = NewsRepository.getNewsWithFulltext(news)
        if (result.isFailure) {
            context.error()
            return
        }
        context.startActivity(CurrentNewsItemActivity.open(context, result.value()))
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
            newsItem.likes.setOnClickListener { context.async { like() } }
            newsItem.newsAuthor.setOnClickListener { onProfileClick(news.author.id) }
            if (news.canDelete) {
                newsItem.deleteButton.visibility = View.VISIBLE
                newsItem.deleteButton.setOnClickListener { context.async { onNewsDelete(news) } }
            }
        }

        private fun setLikeLogo() {
            val id = if (liked)
                R.drawable.heartred
            else
                R.drawable.heart
            newsItem.likeLogo.setImageResource(id)
        }

        private suspend fun like() {
            val success = if (!liked)
                NewsRepository.tryLikeNews(news.id)
            else
                NewsRepository.tryDislikeNews(news.id)
            if (!success) {
                context.error()
                return
            }
            liked = !liked
            likesCount += if (liked) 1 else -1
            setLikeLogo()
            newsItem.likesCount.text = likesCount.toString()
        }

        private fun onProfileClick(id: UUID) {
            if (!CredentialsManager.isAuthenticated()) {
                return
            }
            context.startActivity(ProfileActivity.openProfile(context, id))
        }

        private suspend fun onNewsDelete(currentNews: News) {
            if (NewsRepository.tryDeleteNews(currentNews.id)) {
                val index = this@NewsFeedAdapter.news.indexOf(currentNews)
                this@NewsFeedAdapter.news.remove(currentNews)
                notifyItemRemoved(index)
                return
            }
            context.error()
        }
    }
}
