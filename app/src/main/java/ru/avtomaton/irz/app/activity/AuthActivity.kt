package ru.avtomaton.irz.app.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.databinding.ActivityAuthBinding
import ru.avtomaton.irz.app.databinding.ForgotPasswordDialogBinding
import ru.avtomaton.irz.app.model.pojo.Credentials
import ru.avtomaton.irz.app.model.pojo.Email
import ru.avtomaton.irz.app.model.repository.AuthRepository
import ru.avtomaton.irz.app.services.CredentialsManager

/**
 * @author Anton Akkuzin
 */
class AuthActivity : AppCompatActivityBase() {

    private lateinit var awaitMessage: String
    private lateinit var buttonName: String
    private lateinit var missingEmail: String
    private lateinit var missingPassword: String
    private lateinit var authFailure: String
    private lateinit var authError: String
    private lateinit var dialogMissingEmail: String
    private lateinit var dialogSuccess: String
    private lateinit var dialogFail:String

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(BackPressedCallback())

        binding = ActivityAuthBinding.inflate(layoutInflater)
        binding.authButton.setOnClickListener { this.lifecycleScope.launch { auth() } }
        binding.forgotPasswordButton.setOnClickListener { showPasswordDialog() }

        awaitMessage = getString(R.string.auth_btn_await_message)
        buttonName = getString(R.string.auth_activity_sign_in_button_title)
        missingEmail = getString(R.string.auth_missing_email)
        missingPassword = getString(R.string.auth_missing_password)
        authFailure = getString(R.string.auth_failure_message)
        authError = getString(R.string.auth_error_message)
        dialogMissingEmail = getString(R.string.auth_activity_forgot_password_missing_email)
        dialogSuccess = getString(R.string.auth_activity_forgot_password_success)
        dialogFail = getString(R.string.auth_activity_forgot_password_fail)


        setContentView(binding.root)
    }

    private suspend fun auth() {
        val authBody = extractCredentials() ?: return
        binding.authButton.text = awaitMessage
        val logged: Boolean
        try {
            logged = CredentialsManager.login(authBody)
        } catch (ex: Throwable) {
            Log.e(tag, "Authenticate request failed with exception", ex)
            warn(authError)
            return
        }
        if (!logged) {
            warn(authFailure)
            binding.authButton.text = buttonName
            return
        }

        startActivity(NewsActivity.openNews(this))
    }

    private fun extractCredentials(): Credentials? {
        val email = binding.email.text.toString()
        if (email.isEmpty() || email.isBlank()) {
            warn(missingEmail)
            return null
        }
        val password = binding.password.text.toString()
        if (password.isEmpty() || password.isBlank()) {
            warn(missingPassword)
            return null
        }
        return Credentials(email, password)
    }

    private fun showPasswordDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        val dialogBinding: ForgotPasswordDialogBinding = ForgotPasswordDialogBinding.inflate(layoutInflater)
        alertDialog.setView(dialogBinding.root)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            val email = dialogBinding.email.text.toString()
            if (email.isEmpty() || email.isBlank()) {
                warn(dialogMissingEmail)
                return@setButton
            }
            this.lifecycleScope.launch {
                val success = AuthRepository.resetPassword(Email(email))
                if (success) {
                    warn(dialogSuccess)
                } else {
                    warn(dialogFail)
                }
            }
        }
        alertDialog.show()
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