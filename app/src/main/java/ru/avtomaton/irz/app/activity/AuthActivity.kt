package ru.avtomaton.irz.app.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Response
import ru.avtomaton.irz.app.R
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens

/**
 * @author Anton Akkuzin
 */
class AuthActivity : AppCompatActivity() {

    private val tag : String = "[Auth]"

    private var emailField : EditText? = null
    private var passwordField : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailField = findViewById(R.id.auth_email)
        passwordField = findViewById(R.id.auth_password)

        findViewById<Button>(R.id.auth_request_btn).setOnClickListener { auth() }
    }

    private fun auth() {
        val email = emailField!!.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(
                this,
                "Не забудьте указать почту!",
                Toast.LENGTH_SHORT).show()
            return
        }
        val password = passwordField!!.text.toString()
        if (password.isEmpty()) {
            Toast.makeText(
                this,
                "Обязательно укажите пароль!",
                Toast.LENGTH_SHORT).show()
            return
        }

        IrzClient.authApi.authenticate(AuthBody(email, password)).enqueue(Callback())
    }

    inner class Callback : retrofit2.Callback<JwtTokens> {
        override fun onResponse(call: Call<JwtTokens>, response: Response<JwtTokens>) {
            if (response.isSuccessful) {
                val jwtToken = response.body()?.jwtToken
                val refreshToken = response.body()?.refreshToken
                Log.i(tag, "Successfully authenticated!")
                Log.i(tag, "tokens are jwt=[${jwtToken}], refresh=[${refreshToken}].")
                finish()
            } else {
                Toast.makeText(
                    this@AuthActivity,
                    "Мы Вас не узанали, попробуйте ещё раз!",
                    Toast.LENGTH_SHORT).show()
                val code = response.code()
                val body = response.body().toString()
                Log.w(tag, "Auth failed with code=${code}, body=${body}.")
            }
        }

        override fun onFailure(call: Call<JwtTokens>, t: Throwable) {
            Toast.makeText(
                this@AuthActivity,
                "Нет связи с сервером!",
                Toast.LENGTH_SHORT).show()
            Log.e(tag, "Error: ", t)
        }

    }
}