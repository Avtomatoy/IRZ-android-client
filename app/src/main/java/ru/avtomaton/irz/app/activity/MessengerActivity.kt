package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.avtomaton.irz.app.activity.util.MessengerAdapter
import ru.avtomaton.irz.app.databinding.ActivityMessengerBinding
import ru.avtomaton.irz.app.model.repository.UserRepository
import java.util.*

/**
 * @author Anton Akkuzin
 */
class MessengerActivity : NavbarAppCompatActivityBase(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMessengerBinding
    private var userId: UUID? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessengerBinding.inflate(layoutInflater).apply {
            chatsRefreshLayout.setOnRefreshListener(this@MessengerActivity)
            newsButton.setOnClickListener { onNewsClick() }
            messengerButton.setOnClickListener { chatsFeed.scrollToPosition(0) }
            searchButton.setOnClickListener { onSearchClick() }
            profileButton.setOnClickListener { onProfileClick() }
            setContentView(root)
        }
    }

    override fun onResume() {
        super.onResume()
        async {
            UserRepository.getMe().letIfSuccess { userId = id }
            recreateAdapter()
        }
    }

    private fun recreateAdapter() {
        binding.chatsFeed.apply {
            layoutManager = LinearLayoutManager(this@MessengerActivity)
            adapter = MessengerAdapter(this@MessengerActivity, userId!!)
        }
    }

    override fun onRefresh() {
        recreateAdapter()
        binding.chatsRefreshLayout.isRefreshing = false
    }

    override fun onMessengerClick() {
        binding.chatsFeed.scrollToPosition(0)
    }

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, MessengerActivity::class.java)
        }
    }
}