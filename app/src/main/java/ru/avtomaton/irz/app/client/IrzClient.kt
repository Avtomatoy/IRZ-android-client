package ru.avtomaton.irz.app.client

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.images.ImagesApi
import ru.avtomaton.irz.app.client.api.news.NewsApi
import ru.avtomaton.irz.app.client.api.users.UsersApi
import ru.avtomaton.irz.app.client.infra.AuthInterceptor

/**
 * @author Anton Akkuzin
 */
object IrzClient {
    val authApi: AuthApi
    val usersApi: UsersApi
    val newsApi: NewsApi
    val imagesApi: ImagesApi

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
        newsApi = retrofit.create(NewsApi::class.java)
        imagesApi = retrofit.create(ImagesApi::class.java)
    }
}
