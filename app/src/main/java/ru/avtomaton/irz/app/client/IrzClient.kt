package ru.avtomaton.irz.app.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.users.UsersApi
import ru.avtomaton.irz.app.client.infra.AuthInterceptor
import ru.avtomaton.irz.app.client.infra.CredentialsHolder

/**
 * @author Anton Akkuzin
 */
object IrzClient {
    val authApi : AuthApi
    val usersApi : UsersApi

    private val credentialsHolder: CredentialsHolder

    init {
        credentialsHolder = CredentialsHolder("user@example.com", "string")

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(credentialsHolder))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5249/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
    }


}
