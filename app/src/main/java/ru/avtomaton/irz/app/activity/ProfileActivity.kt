package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.OpResult
import ru.avtomaton.irz.app.client.api.users.UserRepository
import ru.avtomaton.irz.app.client.api.users.models.User
import ru.avtomaton.irz.app.databinding.ActivityProfileBinding
import ru.avtomaton.irz.app.infra.SessionManager
import java.util.*

/**
 * @author Anton Akkuzin
 */
class ProfileActivity : AppCompatActivityBase() {

    private lateinit var errorLoadMessage: String

    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: User
    private var isSubscription: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        errorLoadMessage = getString(R.string.profile_error_on_load)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val possibleId = intent.getStringExtra("id")
        val id: UUID? = if (possibleId!!.isEmpty()) null else UUID.fromString(possibleId)
        binding.sectionMain.visibility = View.GONE
        binding.card.visibility = View.GONE
        setInfo(id)
        if (id == null) {
            binding.profileActionLogo.setImageResource(R.drawable.exit)
            binding.profileActionLogo.setBackgroundColor(
                resources.getColor(
                    R.color.Pantone_200_C,
                    null
                )
            )
            binding.profileActionButton.setOnClickListener { exit() }
        } else {
            binding.profileActionLogo.setImageResource(R.drawable.pin)
        }

        binding.newsButton.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        setContentView(binding.root)
    }

    private fun exit() {
        this.lifecycleScope.launch {
            SessionManager.exit()
            startActivity(Intent(this@ProfileActivity, NewsActivity::class.java))
        }
    }

    private fun onSubscribe() {
        this.lifecycleScope.launch {
            if (isSubscription) {
                IrzClient.usersApi.subscribe(user.id)
            } else {
                IrzClient.usersApi.unsubscribe(user.id)
            }
            isSubscription = !isSubscription
            setSubscriptionBackground()
        }
    }

    private fun setInfo(id: UUID?) {
        this.lifecycleScope.launch {
            val userResult = getUser(id)
            if (userResult.isFailure) {
                Toast.makeText(this@ProfileActivity, errorLoadMessage, Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }
            bindUserInfo(userResult.value())
        }
    }

    private suspend fun getUser(id: UUID?): OpResult<User> {
        return if (id == null)
            UserRepository.getMe()
        else
            UserRepository.getUser(id)
    }

    private fun bindUserInfo(user: User) {
        binding.birthday.text = simpleDateFormat.format(user.birthday)
        binding.aboutMyselfText.text = user.aboutMyself
        binding.profileSkillsText.text = user.skills
        binding.profileMyDoingsText.text = user.myDoings
        user.image?.also { binding.image.setImageBitmap(it) }
        binding.mainPosition.text =
            if (user.positions.isNotEmpty()) user.positions[0].name else "..."
        binding.name.text = user.firstName
        binding.surname.text = user.surname
        binding.patronymic.text = user.patronymic

        if (!user.isMe) {
            binding.profileActionButton.setOnClickListener { this@ProfileActivity.onSubscribe() }
            isSubscription = user.isSubscription
            setSubscriptionBackground()
        }
        this@ProfileActivity.user = user

        binding.sectionMain.visibility = View.VISIBLE
        binding.card.visibility = View.VISIBLE
    }

    private fun setSubscriptionBackground() {
        val color = if (isSubscription)
            R.color.Pantone_200_C
        else
            R.color.Pantone_289_C
        binding.profileActionLogo.setBackgroundColor(resources.getColor(color, null))
    }

    companion object {
        fun openMyProfile(context: Context): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", "")
            return intent
        }

        fun openProfile(context: Context, id: UUID): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("id", id.toString())
            return intent
        }
    }
}