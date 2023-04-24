package ru.avtomaton.irz.app.client

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.avtomaton.irz.app.client.api.*
import java.net.URL
import java.util.Properties

/**
 * @author Anton Akkuzin
 */
object IrzClient {
    lateinit var authApi: AuthApi
    lateinit var usersApi: UsersApi
    lateinit var newsApi: NewsApi
    lateinit var imagesApi: ImagesApi
    lateinit var likesApi: LikesApi
    lateinit var positionsApi: UserPositionsApi
    lateinit var subscriptionsApi: SubscriptionsApi

    fun init(props: Properties) {
        val host = props.getProperty("server.host")!!
        val port = props.getProperty("server.port")!!.toInt()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL("http", host, port, "/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
        newsApi = retrofit.create(NewsApi::class.java)
        imagesApi = retrofit.create(ImagesApi::class.java)
        likesApi = retrofit.create(LikesApi::class.java)
        positionsApi = retrofit.create(UserPositionsApi::class.java)
        subscriptionsApi = retrofit.create(SubscriptionsApi::class.java)
    }
}
