package ru.avtomaton.irz.app.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.users.UsersApi
import ru.avtomaton.irz.app.client.infra.AuthInterceptor

/**
 * @author Anton Akkuzin
 */
object IrzClient {
    val authApi : AuthApi
    val usersApi : UsersApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
    }
}
