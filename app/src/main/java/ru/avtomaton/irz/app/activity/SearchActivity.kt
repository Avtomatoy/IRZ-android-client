package ru.avtomaton.irz.app.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.activity.util.UserSearchParams
import ru.avtomaton.irz.app.activity.util.SearchParamsSpinner.positionSpinner
import ru.avtomaton.irz.app.activity.util.SearchParamsSpinner.rolesSpinner
import ru.avtomaton.irz.app.activity.util.SearchUsersAdapter
import ru.avtomaton.irz.app.databinding.ActivitySearchBinding
import ru.avtomaton.irz.app.databinding.SearchParamsBinding
import ru.avtomaton.irz.app.model.pojo.UserShort
import ru.avtomaton.irz.app.model.repository.PositionRepository
import ru.avtomaton.irz.app.model.repository.UserRepository
import java.util.UUID

/**
 * @author Anton Akkuzin
 */
class SearchActivity : NavbarAppCompatActivityBase(), SearchUsersAdapter.Listener {

    private val pageSize = 20
    private val paramsBuilder: UserSearchParams.Builder = UserSearchParams.Builder()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchUsersAdapter

    private lateinit var positionsMap: HashMap<String, UUID>
    private lateinit var positionsList: List<String>
    private val roles: List<String> = listOf("Support", "CabinetsManager", "Admin")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        binding.searchRequestButton.setOnClickListener { recreateAdapter() }
        binding.searchParamsButton.setOnClickListener { showParamsDialog() }

        binding.newsButton.setOnClickListener { onNewsClick() }
        binding.messengerButton.setOnClickListener { onMessengerClick() }
        binding.zearchButton.setOnClickListener { onSearchClick() }
        binding.eventsButton.setOnClickListener { onEventsClick() }
        binding.profileButton.setOnClickListener { onProfileClick() }

        async {
            val positionsResult = PositionRepository.getPositions()
            if (positionsResult.isFailure) {
                error()
                finish()
                return@async
            }
            positionsMap = positionsResult.value()
            positionsList = positionsResult.value().keys.toList()
            setContentView(binding.root)
        }
    }

    private fun showParamsDialog() {
        SearchParamsBinding.inflate(layoutInflater).apply {
            position.positionSpinner(positionsMap, positionsList, paramsBuilder)
            role.rolesSpinner(roles, paramsBuilder)

            AlertDialog.Builder(this@SearchActivity).create().apply {
                setView(root)
                setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ -> dismiss() }
            }.show()
        }
    }

    private fun recreateAdapter() {
        adapter = SearchUsersAdapter(this)
        this.lifecycleScope.launch { updateUsers(0, pageSize) }
        binding.usersArea.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    private suspend fun updateUsers(pageIndex: Int, pageSize: Int) {
        binding.userInput.query.toString().apply {
            if (this.isNotEmpty() && this.isNotBlank()) {
                paramsBuilder.searchString = this
            } else {
                paramsBuilder.searchString = null
            }
        }
        val params = paramsBuilder.let {
            it.pageIndex = pageIndex
            it.pageSize = pageSize
            it.build()
        }
        UserRepository.getUsers(params).letIfSuccess {
            adapter.updateUsers(this)
        }
    }

    override fun onUserClick(user: UserShort) {
        startActivity(ProfileActivity.openProfile(this, user.id))
    }

    override fun onUsersUpdate(listSize: Int, position: Int) {
        if (listSize % pageSize != 0 || listSize - position != 5) {
            return
        }
        this.lifecycleScope.launch { updateUsers(listSize / pageSize, pageSize) }
    }

    override fun onSearchClick() {
        // do nothing
    }

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}
