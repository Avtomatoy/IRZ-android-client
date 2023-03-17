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
import ru.avtomaton.irz.app.infra.SessionManager

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SessionManager.init(dataStore)
        this.lifecycleScope.launch {
            SessionManager.login()
            startNews()
        }
    }

    private fun startNews() {
        startActivity(Intent(this, NewsActivity::class.java))
    }
}
