package ru.avtomaton.irz.app.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.activity.util.SearchParams
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
    private val paramsBuilder: SearchParams.Builder = SearchParams.Builder()

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

        this.lifecycleScope.launch {
            val positionsResult = PositionRepository.getPositions()
            if (positionsResult.isFailure) {
                error()
                finish()
                return@launch
            }
            positionsMap = positionsResult.value()
            positionsList = positionsResult.value().keys.toList()
            setContentView(binding.root)
        }
    }

    private fun showParamsDialog() {
        val searchParamsBinding = SearchParamsBinding.inflate(layoutInflater)
        searchParamsBinding.position.apply {
            positionSpinner(positionsMap, positionsList, paramsBuilder)
        }
        searchParamsBinding.role.apply {
            rolesSpinner(roles, paramsBuilder)
        }
        AlertDialog.Builder(this).create().apply {
            setView(searchParamsBinding.root)
            setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ -> dismiss() }
        }.show()
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
                paramsBuilder.searchString(this)
            } else {
                paramsBuilder.searchString(null)
            }
        }
        val params = paramsBuilder.pageIndex(pageIndex).pageSize(pageSize).build()
        UserRepository.getUsers(params).applyIfSuccess {
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

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }
}