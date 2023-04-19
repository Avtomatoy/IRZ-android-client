package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.users.UserRepository
import ru.avtomaton.irz.app.client.api.users.models.User
import ru.avtomaton.irz.app.databinding.ActivityProfileBinding
import ru.avtomaton.irz.app.infra.SessionManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Anton Akkuzin
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val userRepository: UserRepository = UserRepository()
    private val dateFormat: SimpleDateFormat =
        SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale("ru"))
    private lateinit var user: User
    private var isSubscription: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            SessionManager.dropCredentials()
        }
        startActivity(Intent(this, NewsActivity::class.java))
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
            val user = if (id == null) userRepository.getMe()!! else userRepository.getUser(id)!!
            binding.birthday.text = dateFormat.format(user.birthday)
            binding.aboutMyselfText.text = user.aboutMyself
            binding.profileSkillsText.text = user.skills
            binding.profileMyDoingsText.text = user.myDoings
            user.image?.also { binding.image.setImageBitmap(it) }
            binding.mainPosition.text = if (user.positions.isNotEmpty()) user.positions[0].name else "..."
            binding.name.text = user.firstName
            binding.surname.text = user.surname
            binding.patronymic.text = user.patronymic

            if (id != null) {
                binding.profileActionButton.setOnClickListener { this@ProfileActivity.onSubscribe() }
                isSubscription = user.isSubscription
                setSubscriptionBackground()
            }
            this@ProfileActivity.user = user

            binding.sectionMain.visibility = View.VISIBLE
            binding.card.visibility = View.VISIBLE
        }
    }

    private fun setSubscriptionBackground() {
        if (isSubscription) {
            binding.profileActionLogo.setBackgroundColor(
                resources.getColor(
                    R.color.Pantone_200_C,
                    null
                )
            )
        } else {
            binding.profileActionLogo.setBackgroundColor(
                resources.getColor(
                    R.color.Pantone_289_C,
                    null
                )
            )
        }
    }
}