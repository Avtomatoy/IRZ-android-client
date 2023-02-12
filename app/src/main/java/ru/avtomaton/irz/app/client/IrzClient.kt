package ru.avtomaton.irz.app.client

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.auth.models.AuthResponse
import ru.avtomaton.irz.app.client.infra.AuthInterceptor
import ru.avtomaton.irz.app.client.infra.CredentialsHolder

/**
 * @author Anton Akkuzin
 */
class IrzClient(baseUrl : String, private val credentialsHolder: CredentialsHolder) {

    private val authApi : AuthApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(credentialsHolder))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
    }

    fun authenticate() {
        authApi.authenticate(credentialsHolder.getCredentials()).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    Log.d("some-tag xz", "Successfully logged.")
                    Log.d("tokens", "jwt=[${response.body()?.jwtToken.orEmpty()}]\nrefresh=[${response.body()?.refreshToken.orEmpty()}]")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                throw t
            }

        })
    }
}
