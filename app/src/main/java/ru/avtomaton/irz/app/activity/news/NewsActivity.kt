package ru.avtomaton.irz.app.activity.news

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.activity.news.util.NewsFeedAdapter
import ru.avtomaton.irz.app.client.api.news.NewsRepository
import ru.avtomaton.irz.app.databinding.ActivityNewsBinding

/**
 * @author Anton Akkuzin
 */
class NewsActivity : AppCompatActivity(), NewsFeedAdapter.Updater, SwipeRefreshLayout.OnRefreshListener {

    private val pageSize = 10

    private val newsRepository: NewsRepository = NewsRepository()

    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsFeedAdapter: NewsFeedAdapter
    private lateinit var newsRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        newsRefreshLayout = binding.newsRefreshLayout
        newsRefreshLayout.setOnRefreshListener(this)
        binding.newsName.setOnClickListener { scrollToActivityTop() }
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
        newsRepository.getNews(pageIndex, pageSize)?.also { newsFeedAdapter.updateNews(it) }
    }

    inner class DoNothingBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {}
    }

    override fun onRefresh() {
        setupAdapter()
        newsRefreshLayout.isRefreshing = false
    }
}