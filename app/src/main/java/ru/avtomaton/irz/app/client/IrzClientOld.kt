package ru.avtomaton.irz.app.client

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.client.api.users.UsersApi
import ru.avtomaton.irz.app.client.api.users.models.MeResponse
import ru.avtomaton.irz.app.client.infra.AuthInterceptor
import ru.avtomaton.irz.app.client.infra.CredentialsHolder

/**
 * @author Anton Akkuzin
 */
class IrzClientOld(baseUrl : String, private val credentialsHolder: CredentialsHolder) {

    private val irzClientLogTag: String = "IrzClient"
    private val authApi : AuthApi
    private val usersApi : UsersApi

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
        usersApi = retrofit.create(UsersApi::class.java)
    }

    fun authenticate(): Boolean {
        var result = false
        authApi.authenticate(credentialsHolder.getCredentials())
            .enqueue(object : Callback<JwtTokens> {
            override fun onResponse(call: Call<JwtTokens>, response: Response<JwtTokens>) {
                Log.d(irzClientLogTag, "auth status code = ${response.code()}")
                if (response.isSuccessful) {
                    credentialsHolder.setTokens(response.body()!!)
                    result = true
                } else {
                    Log.d(irzClientLogTag, response.body()!!.toString())
                }
            }

            override fun onFailure(call: Call<JwtTokens>, t: Throwable) {
                throw t
            }
        })
        return result
    }

    fun me() : MeResponse? {
        var meResponse : MeResponse? = null
        usersApi.me().enqueue(object : Callback<MeResponse> {
            override fun onResponse(call: Call<MeResponse>, response: Response<MeResponse>) {
                if (response.isSuccessful) {
                    meResponse = response.body()!!
                }
            }

            override fun onFailure(call: Call<MeResponse>, t: Throwable) {
                throw t
            }
        })
        return meResponse
    }
}
