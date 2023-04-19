package ru.avtomaton.irz.app.client

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.auth.AuthApi
import ru.avtomaton.irz.app.client.api.images.ImagesApi
import ru.avtomaton.irz.app.client.api.likes.LikesApi
import ru.avtomaton.irz.app.client.api.news.NewsApi
import ru.avtomaton.irz.app.client.api.users.UsersApi
import ru.avtomaton.irz.app.client.infra.AuthInterceptor
import java.net.URL

/**
 * @author Anton Akkuzin
 */
object IrzClient {
    lateinit var authApi: AuthApi
    lateinit var usersApi: UsersApi
    lateinit var newsApi: NewsApi
    lateinit var imagesApi: ImagesApi
    lateinit var likesApi: LikesApi

    init {
        init()
    }

    fun init() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL("http", "10.0.2.2", 5249, "/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
        newsApi = retrofit.create(NewsApi::class.java)
        imagesApi = retrofit.create(ImagesApi::class.java)
        likesApi = retrofit.create(LikesApi::class.java)
    }

    fun reinit() {
        init()
    }
}

object IpHolder {
    var ip: String? = null

}
