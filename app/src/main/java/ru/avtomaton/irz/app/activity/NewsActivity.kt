package ru.avtomaton.irz.app.activity

import android.app.AlertDialog
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
import ru.avtomaton.irz.app.activity.util.SearchParamsSpinner.userSpinner
import ru.avtomaton.irz.app.activity.util.UserSearchParams
import ru.avtomaton.irz.app.databinding.ActivityNewsBinding
import ru.avtomaton.irz.app.databinding.NewsSearchParamsBinding
import ru.avtomaton.irz.app.model.pojo.UserShort
import ru.avtomaton.irz.app.model.repository.UserRepository
import ru.avtomaton.irz.app.services.CredentialsManager

/**
 * @author Anton Akkuzin
 */
class NewsActivity : NavbarAppCompatActivityBase(), SwipeRefreshLayout.OnRefreshListener {

    private val postRequestCode = 228

    private lateinit var binding: ActivityNewsBinding
    private lateinit var authors: List<UserShort>
    private val builder: NewsSearchParams.Builder = NewsSearchParams.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(DoNothingBackPressedCallback())
        if (CredentialsManager.isAuthenticated()) {
            async {
                UserRepository.getUsers(
                    UserSearchParams.Builder()
                        .apply { pageSize = 100 }.build()
                )
                    .letIfSuccess { authors = this }
            }
        }
        binding = ActivityNewsBinding.inflate(layoutInflater).apply {
            newsRefreshLayout.setOnRefreshListener(this@NewsActivity)
            newsButton.setOnClickListener { onNewsClick() }
            messengerButton.setOnClickListener { onMessengerClick() }
            searchButton.setOnClickListener { onSearchClick() }
            eventsButton.setOnClickListener { onEventsClick() }
            profileButton.setOnClickListener { onProfileClick() }
            newsActivityName.setOnClickListener { scrollToActivityTop() }
            searchParamsButton.setOnClickListener { searchDialog() }
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
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        recreateAdapter()
    }

    private fun scrollToActivityTop() {
        binding.newsFeed
        binding.newsFeed.scrollToPosition(0)
    }

    private fun recreateAdapter() {
        binding.newsFeed.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = NewsFeedAdapter(this@NewsActivity, builder)
        }
    }

    private fun searchDialog() {
        NewsSearchParamsBinding.inflate(layoutInflater).apply {
            if (CredentialsManager.isAuthenticated()) {
                author.userSpinner(authors, builder)
            } else {
                authorizedArea.visibility = View.GONE
            }
            AlertDialog.Builder(this@NewsActivity).create().apply {
                setView(root)
                setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
                    builder.publicOnly = isPublic.isChecked
                    builder.likedOnly = isLiked.isChecked
                    builder.searchString = searchInput.text.toString().let {
                        it.ifBlank { null }
                    }
                    recreateAdapter()
                }
                setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена") { _, _ ->
                    builder.apply {
                        authorId = null
                        publicOnly = false
                        likedOnly = false
                        searchString = null
                    }
                }
            }.show()
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