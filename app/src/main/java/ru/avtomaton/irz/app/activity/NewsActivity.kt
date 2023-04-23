package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.util.NewsFeedAdapter
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.news.NewsRepository
import ru.avtomaton.irz.app.client.api.news.models.News
import ru.avtomaton.irz.app.databinding.ActivityNewsBinding
import ru.avtomaton.irz.app.infra.SessionManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class NewsActivity :
    AppCompatActivityBase(),
    NewsFeedAdapter.NewsFeedAdapterListener,
    SwipeRefreshLayout.OnRefreshListener {

    private val pageSize = 10
    private val postRequestCode = 228

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var newsRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        newsRefreshLayout = binding.newsRefreshLayout
        newsRefreshLayout.setOnRefreshListener(this)
        binding.newsName.setOnClickListener { scrollToActivityTop() }
        binding.newsButton.setOnClickListener { scrollToActivityTop() }
        binding.writeNewsButton.setOnClickListener {
            startActivityForResult(Intent(this, PostNewsActivity::class.java), postRequestCode)
        }

        if (SessionManager.isAuthenticated()) {
            binding.profileButton.setOnClickListener {
                startActivity(ProfileActivity.openMyProfile(this))
            }
        } else {
            binding.writeNewsButton.visibility = View.GONE
            binding.profileButton.setOnClickListener {
                startActivity(Intent(this, AuthActivity::class.java))
            }
            val colorStateList = ContextCompat.getColorStateList(this, R.color.Cool_Grey_3)
            binding.messagesButton.backgroundTintList = colorStateList
            binding.searchButton.backgroundTintList = colorStateList
            binding.calendarButton.backgroundTintList = colorStateList
        }
        onBackPressedDispatcher.addCallback(DoNothingBackPressedCallback())
        setupAdapter()
        setContentView(binding.root)
    }

    private fun scrollToActivityTop() {
        binding.newsFeed.scrollToPosition(0)
    }

    private fun setupAdapter() {
        newsFeedAdapter = NewsFeedAdapter(this)
        this.lifecycleScope.launch { updateNews(0, pageSize) }
        binding.newsFeed.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsFeedAdapter
        }
    }

    override fun onUpdate(listSize: Int, position: Int) {
        if (listSize % pageSize != 0 || listSize - position != 5)
            return
        this.lifecycleScope.launch { updateNews(listSize / pageSize, pageSize) }
    }

    private suspend fun updateNews(pageIndex: Int, pageSize: Int) {
        val result = NewsRepository.getNews(pageIndex, pageSize)
        if (result.isFailure) {
            onLoadErrorMessage()
            return
        }
        newsFeedAdapter.updateNews(result.value())
    }

    inner class DoNothingBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {}
    }

    override fun onRefresh() {
        setupAdapter()
        newsRefreshLayout.isRefreshing = false
    }

    override fun onLike(newsId: UUID) {
        this.lifecycleScope.launch {
            try {
                IrzClient.likesApi.like(newsId)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    override fun onDislike(newsId: UUID) {
        this.lifecycleScope.launch {
            try {
                IrzClient.likesApi.dislike(newsId)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    override fun onNewsClick(news: News) {
        this.lifecycleScope.launch {
            val result = NewsRepository.getNewsWithFulltext(news)
            if (result.isFailure) {
                onLoadErrorMessage()
                return@launch
            }
            startActivity(CurrentNewsItemActivity.open(this@NewsActivity, result.value()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("check")
        println("result=$resultCode,request=$requestCode")
        if (resultCode == RESULT_OK && requestCode == postRequestCode) {
            print("update")
            setupAdapter()
        }
    }

    override fun onProfileClick(id: UUID) {
        startActivity(ProfileActivity.openProfile(this, id))
    }

    override fun onNewsDelete(news: News) {
        this.lifecycleScope.launch {
            val response: Response<Unit>
            try {
                response = IrzClient.newsApi.deleteNews(news.id)
            } catch (ex: Throwable) {
                ex.printStackTrace()
                return@launch
            }
            if (response.isSuccessful) {
                newsFeedAdapter.deleteNews(news)
            }
        }
    }
}