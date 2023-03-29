package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.databinding.ActivityProfileBinding
import ru.avtomaton.irz.app.infra.SessionManager

/**
 * @author Anton Akkuzin
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        binding.newsButton.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
        binding.exitButton.setOnClickListener {
            exit()
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
}