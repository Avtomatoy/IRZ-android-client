package ru.avtomaton.irz.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.activity.NewsActivity
import ru.avtomaton.irz.app.client.IpHolder
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.databinding.ActivityMainBinding
import ru.avtomaton.irz.app.infra.SessionManager
import ru.avtomaton.irz.app.infra.UserManager

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * 1Az$dsdwdadqwdqds
 * Задачи:
 * 1) начать хранить креды, подгружать их после перезапуска приложения (shared preferences);
 * -2) сохранять токены при авторизации;
 * 3) личный кабинет со всем функционалом;
 *  а) ...
 *  б) ...
 * 4) Cabinets api
 * 5) Events api
 * 6) Новости
 *  а) комментарии
 *  б) лайки
 *
 *
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.ipConfirm.setOnClickListener { onClick() }
        setContentView(binding.root)
    }

    private fun onClick() {
        val ip = binding.ipAddress.text.toString()
        println("got ip $ip")
        IpHolder.ip = ip
        IrzClient.reinit()
        SessionManager.init(dataStore)
        this.lifecycleScope.launch {
            val login = SessionManager.login()
            println("login result = $login")
            if (login) {
                UserManager.downloadInfo()
            }
            startNews()
        }
    }

    private fun startNews() {
        startActivity(Intent(this, NewsActivity::class.java))
    }
}
