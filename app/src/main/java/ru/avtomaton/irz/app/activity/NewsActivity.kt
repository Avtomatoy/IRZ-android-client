package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.util.DoNothingBackPressedCallback
import ru.avtomaton.irz.app.activity.util.NewsFeedAdapter
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.model.repository.NewsRepository
import ru.avtomaton.irz.app.model.pojo.News
import ru.avtomaton.irz.app.databinding.ActivityNewsBinding
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsActivity :
    NavbarAppCompatActivityBase(),
    NewsFeedAdapter.NewsFeedAdapterListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val pageSize = 10
    private val postRequestCode = 228

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var newsRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(DoNothingBackPressedCallback())

        binding = ActivityNewsBinding.inflate(layoutInflater)

        newsRefreshLayout = binding.newsRefreshLayout
        newsRefreshLayout.setOnRefreshListener(this)

        binding.newsButton.setOnClickListener { onNewsClick() }
        binding.messengerButton.setOnClickListener { onMessengerClick() }
        binding.searchButton.setOnClickListener { onSearchClick() }
        binding.eventsButton.setOnClickListener { onEventsClick() }
        binding.profileButton.setOnClickListener { onProfileClick() }

        binding.newsActivityName.setOnClickListener { scrollToActivityTop() }
        binding.writeNewsButton.setOnClickListener {
            startActivityForResult(PostNewsActivity.open(this), postRequestCode)
        }

        if (!CredentialsManager.isAuthenticated()) {
            binding.writeNewsButton.visibility = View.GONE
            val color = ContextCompat.getColorStateList(this, R.color.Cool_Grey_3)
            binding.messengerButton.backgroundTintList = color
            binding.searchButton.backgroundTintList = color
            binding.eventsButton.backgroundTintList = color

            binding.messengerButton.isClickable = false
            binding.searchButton.isClickable = false
            binding.eventsButton.isClickable = false
        }

        recreateAdapter()
        setContentView(binding.root)
    }

    private fun scrollToActivityTop() {
        binding.newsFeed.scrollToPosition(0)
    }

    private fun recreateAdapter() {
        newsFeedAdapter = NewsFeedAdapter(this)
        this.lifecycleScope.launch { updateNews(0, pageSize) }
        binding.newsFeed.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsFeedAdapter
        }
    }

    private suspend fun updateNews(pageIndex: Int, pageSize: Int) {
        val result = NewsRepository.getNews(pageIndex, pageSize)
        if (result.isFailure) {
            error()
            return
        }
        newsFeedAdapter.updateNews(result.value())
    }

    override fun onRefresh() {
        recreateAdapter()
        newsRefreshLayout.isRefreshing = false
    }

    // section: NewsFeedAdapter.NewsFeedAdapterListener
    override fun onNewsUpdate(listSize: Int, position: Int) {
        if (listSize % pageSize != 0 || listSize - position != 5)
            return
        this.lifecycleScope.launch { updateNews(listSize / pageSize, pageSize) }
    }

    override fun onNewsLike(newsId: UUID, liked: Boolean) {
        this.lifecycleScope.launch {
            try {
                if (liked) {
                    IrzClient.likesApi.like(newsId)
                    return@launch
                }
                IrzClient.likesApi.dislike(newsId)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    override fun onNewsClick(news: News) {
        if (!CredentialsManager.isAuthenticated()) {
            return
        }
        this.lifecycleScope.launch {
            val result = NewsRepository.getNewsWithFulltext(news)
            if (result.isFailure) {
                error()
                return@launch
            }
            startActivity(CurrentNewsItemActivity.open(this@NewsActivity, result.value()))
        }
    }

    override fun onNewsDelete(news: News) {
        this.lifecycleScope.launch {
            if (NewsRepository.tryDeleteNews(news.id)) {
                newsFeedAdapter.deleteNews(news)
                return@launch
            }
            error()
        }
    }

    override fun onProfileClick(id: UUID) {
        startActivity(ProfileActivity.openProfile(this, id))
    }
    // end section: NewsFeedAdapter.NewsFeedAdapterListener

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == postRequestCode) {
            recreateAdapter()
        }
    }

    override fun onNewsClick() {
        scrollToActivityTop()
    }

    override fun onProfileClick() {
        val intent = if (CredentialsManager.isAuthenticated())
            ProfileActivity.openMyProfile(this)
        else
            AuthActivity.open(this)
        startActivity(intent)
    }

    companion object {
        fun openNews(context: Context): Intent {
            return Intent(context, NewsActivity::class.java)
        }

    }
}