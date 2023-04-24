package ru.avtomaton.irz.app.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.model.pojo.Credentials
import ru.avtomaton.irz.app.services.CredentialsManager

/**
 * @author Anton Akkuzin
 */
class AuthActivity : AppCompatActivityBase() {

    private val tag: String = "[Auth]"

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var button: Button

    private lateinit var awaitMessage: String
    private lateinit var authBtnMessage: String
    private lateinit var missingEmail: String
    private lateinit var missingPassword: String
    private lateinit var authFailure: String
    private lateinit var authError: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailField = findViewById(R.id.auth_email)
        passwordField = findViewById(R.id.auth_password)

        button = findViewById(R.id.auth_request_btn)
        awaitMessage = getString(R.string.auth_btn_await_message)
        authBtnMessage = getString(R.string.auth_btn_message)
        missingEmail = getString(R.string.auth_missing_email)
        missingPassword = getString(R.string.auth_missing_password)
        authFailure = getString(R.string.auth_failure_message)
        authError = getString(R.string.auth_error_message)

        button.setOnClickListener { auth() }
        onBackPressedDispatcher.addCallback(BackPressedCallback())
    }

    private fun auth() {
        val authBody = extractCredentials() ?: return
        this.lifecycleScope.launch {
            button.text = awaitMessage
            val logged: Boolean
            try {
                logged = CredentialsManager.login(authBody)
            } catch (ex: Throwable) {
                onAuthError(ex)
                return@launch
            }
            if (logged) {
                onAuthSuccess()
                return@launch
            }
            onAuthFailure()
            button.text = authBtnMessage
        }
    }

    private fun extractCredentials(): Credentials? {
        val email = emailField.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, missingEmail, Toast.LENGTH_SHORT).show()
            return null
        }
        val password = passwordField.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(this, missingPassword, Toast.LENGTH_SHORT).show()
            return null
        }
        return Credentials(email, password)
    }

    private fun onAuthSuccess() {
        startActivity(Intent(this, NewsActivity::class.java))
    }

    private fun onAuthFailure() {
        Toast.makeText(this, authFailure, Toast.LENGTH_SHORT).show()
    }

    private fun onAuthError(t: Throwable) {
        Toast.makeText(this, authError, Toast.LENGTH_SHORT).show()
        Log.e(tag, "Error: ", t)
    }

    inner class BackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(NewsActivity.openNews(this@AuthActivity))
        }
    }

    companion object {

        fun open(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }
}