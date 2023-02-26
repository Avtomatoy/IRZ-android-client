package ru.avtomaton.irz.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import ru.avtomaton.irz.app.activity.AuthActivity
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.infra.SessionManager
import java.util.Objects

/**
 * Задачи:
 * 1) начать хранить креды, подгружать их после перезапуска приложения (shared preferences);
 * -2) сохранять токены при авторизации;
 * 3) ...
 *
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {

    private val tag : String = "[Main]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!SessionManager.isCredentialsLoaded()) {
            SessionManager.setCredentials(loadCredentials())
            SessionManager.markCredentialsLoaded()
        }

        if (Objects.isNull(SessionManager.getCredentials())) {
            setContentView(R.layout.activity_main)
        } else {
            // другая полная вьюха со всеми кнопками
        }

        findViewById<Button>(R.id.auth_btn).setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    fun loadCredentials() : AuthBody? {
        TODO()
    }
}