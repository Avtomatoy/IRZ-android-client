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
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.databinding.ActivityMainBinding
import ru.avtomaton.irz.app.infra.SessionManager
import ru.avtomaton.irz.app.infra.UserManager
import java.util.Properties

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
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val props = Properties()
        props.load(resources.openRawResource(R.raw.application))
        IrzClient.init(props)

        SessionManager.init(dataStore)
        this.lifecycleScope.launch {
            var login = false
            try {
                login = SessionManager.login()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
            if (login) {
                UserManager.downloadInfo()
            }
            startActivity(Intent(this@MainActivity, NewsActivity::class.java))
        }
    }
}
