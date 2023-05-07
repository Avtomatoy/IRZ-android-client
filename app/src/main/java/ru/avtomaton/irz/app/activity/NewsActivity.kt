package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.activity.util.DoNothingBackPressedCallback
import ru.avtomaton.irz.app.activity.util.NewsFeedAdapter
import ru.avtomaton.irz.app.activity.util.NewsSearchParams
import ru.avtomaton.irz.app.databinding.ActivityNewsBinding
import ru.avtomaton.irz.app.services.CredentialsManager

/**
 * @author Anton Akkuzin
 */
class NewsActivity : NavbarAppCompatActivityBase(), SwipeRefreshLayout.OnRefreshListener {

    private val postRequestCode = 228

    private lateinit var binding: ActivityNewsBinding
    private val builder: NewsSearchParams.Builder = NewsSearchParams.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(DoNothingBackPressedCallback())

        binding = ActivityNewsBinding.inflate(layoutInflater).apply {
            newsRefreshLayout.setOnRefreshListener(this@NewsActivity)
            newsButton.setOnClickListener { onNewsClick() }
            messengerButton.setOnClickListener { onMessengerClick() }
            searchButton.setOnClickListener { onSearchClick() }
            eventsButton.setOnClickListener { onEventsClick() }
            profileButton.setOnClickListener { onProfileClick() }
            newsActivityName.setOnClickListener { scrollToActivityTop() }
            writeNewsButton.setOnClickListener {
                @Suppress("DEPRECATION") startActivityForResult(
                    PostNewsActivity.open(this@NewsActivity), postRequestCode
                )
            }
            if (CredentialsManager.isAuthenticated()) {
                return@apply
            }
            val color = ContextCompat.getColorStateList(
                this@NewsActivity, R.color.Cool_Grey_3
            )
            writeNewsButton.visibility = View.GONE
            messengerButton.backgroundTintList = color
            searchButton.backgroundTintList = color
            eventsButton.backgroundTintList = color
            messengerButton.isClickable = false
            searchButton.isClickable = false
            eventsButton.isClickable = false
        }
        recreateAdapter()
        setContentView(binding.root)
    }

    private fun scrollToActivityTop() {
        binding.newsFeed
        val scrollToPosition = binding.newsFeed.scrollToPosition(0)
    }

    private fun recreateAdapter() {
        binding.newsFeed.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = NewsFeedAdapter(this@NewsActivity)
        }
    }

    override fun onRefresh() {
        recreateAdapter()
        binding.newsRefreshLayout.isRefreshing = false
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == postRequestCode) {
            recreateAdapter()
        }
    }

    override fun onNewsClick() {
        scrollToActivityTop()
    }

    override fun onProfileClick() {
        val intent = if (CredentialsManager.isAuthenticated()) ProfileActivity.openMyProfile(this)
        else AuthActivity.open(this)
        startActivity(intent)
    }

    companion object {
        fun openNews(context: Context): Intent {
            return Intent(context, NewsActivity::class.java)
        }

    }
}