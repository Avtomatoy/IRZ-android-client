package ru.avtomaton.irz.app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.MainActivity
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.api.auth.AuthRepository
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.infra.SessionManager

/**
 * @author Anton Akkuzin
 */
class AuthActivity : AppCompatActivity() {

    private val tag : String = "[Auth]"

    private lateinit var emailField : EditText
    private lateinit var passwordField : EditText
    private lateinit var button : Button

    private lateinit var awaitMessage : String
    private lateinit var authBtnMessage : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailField = findViewById(R.id.auth_email)
        passwordField = findViewById(R.id.auth_password)

        button = findViewById(R.id.auth_request_btn)
        awaitMessage = getString(R.string.auth_btn_await_message)
        authBtnMessage = getString(R.string.auth_btn_message)

        button.setOnClickListener { auth() }
        onBackPressedDispatcher.addCallback(BackPressedCallback())
    }

    private fun auth() {
        val email = emailField.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(
                this,
                "Не забудьте указать почту!",
                Toast.LENGTH_SHORT).show()
            return
        }
        val password = passwordField.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(
                this,
                "Обязательно укажите пароль!",
                Toast.LENGTH_SHORT).show()
            return
        }
        this.lifecycleScope.launch {
            button.text = awaitMessage
            val authBody = AuthBody(email, password)
            val result = AuthRepository.auth(authBody)
            if (result.isFailure) {
                onAuthError(result.exceptionOrNull()!!)
            }
            if (result.isSuccess) {
                if (result.getOrNull()!!) {
                    SessionManager.setCredentials(authBody)
                    onAuthSuccess()
                } else {
                    onAuthFailure()
                }
            }
            button.text = authBtnMessage
        }
    }

    private fun onAuthSuccess() {
        startActivity(Intent(
            this@AuthActivity,
            MainActivity::class.java)
        )
    }

    private fun onAuthFailure() {
        Toast.makeText(
            this@AuthActivity,
            "Мы Вас не узанали, попробуйте ещё раз!",
            Toast.LENGTH_SHORT).show()
    }

    private fun onAuthError(t: Throwable) {
        Toast.makeText(
            this@AuthActivity,
            "Нет связи с сервером!",
            Toast.LENGTH_SHORT).show()
        Log.e(tag, "Error: ", t)
    }

    inner class BackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(
                Intent(this@AuthActivity, NewsActivity::class.java)
            )
        }
    }
}