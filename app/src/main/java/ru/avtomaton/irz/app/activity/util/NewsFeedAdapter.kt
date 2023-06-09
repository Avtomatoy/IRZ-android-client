package ru.avtomaton.irz.app.activity.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.AppCompatActivityBase
import ru.avtomaton.irz.app.activity.CurrentNewsItemActivity
import ru.avtomaton.irz.app.activity.ProfileActivity
import ru.avtomaton.irz.app.client.IrzHttpClient.loadImageBy
import ru.avtomaton.irz.app.databinding.NewsItemBinding
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.model.repository.NewsRepository
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsFeedAdapter(
    private val context: AppCompatActivityBase,
    private val paramsBuilder: NewsSearchParams.Builder
) :
    RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder>() {

    private val news: MutableList<News> = mutableListOf()
    private val pageSize = 10

    init {
        context.async { updateNews(0) }
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
        onNewsUpdate(position)
    }

    private suspend fun updateNews(pageIndex: Int) {
        val result = NewsRepository.getNews(paramsBuilder.let {
            it.pageIndex = pageIndex
            it.pageSize = pageSize
            it.build()
        })
        if (result.isFailure) {
            context.error()
            return
        }
        result.value().forEach {
            this.news.add(it)
            notifyItemInserted(this.news.size - 1)
        }
    }

    private fun onNewsUpdate(position: Int) {
        if (itemCount % pageSize != 0 || itemCount - position != 5)
            return
        context.async { updateNews(itemCount / pageSize) }
    }

    private suspend fun onNewsClick(news: News) {
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

            newsItem.apply {
                likesCount.text = news.likesCount.toString()
                commentsCount.text = news.commentCount.toString()
                newsTitle.text = news.title
                news.author.imageId?.also {
                    Glide.with(context).loadImageBy(it).centerCrop().into(newsAuthorImage)
                }
                newsAuthorName.text = news.author.fullName
                newsDatetime.text = AppCompatActivityBase.dateFormat.format(news.dateTime)
                newsImage.setImageDrawable(null)
                news.imageId?.also { Glide.with(context).loadImageBy(it).into(newsImage) }
                newsText.text = news.text
                deleteButton.visibility = View.GONE
                if (!CredentialsManager.isAuthenticated()) {
                    newsItem.likes.visibility = View.GONE
                    return
                }
                likes.setOnClickListener { context.async { like() } }
                newsAuthor.setOnClickListener { onProfileClick(news.author.id) }
                if (news.canDelete) {
                    deleteButton.visibility = View.VISIBLE
                    deleteButton.setOnClickListener { context.async { onNewsDelete(news) } }
                }
            }
        }

        private fun setLikeLogo() {
            val id = if (liked) R.drawable.heartred else R.drawable.heart
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
            if (CredentialsManager.isAuthenticated()) {
                context.startActivity(ProfileActivity.openProfile(context, id))
            }
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
