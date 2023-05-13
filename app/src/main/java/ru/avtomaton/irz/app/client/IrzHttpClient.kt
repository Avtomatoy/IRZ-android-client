package ru.avtomaton.irz.app.client

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.avtomaton.irz.app.client.api.*
import java.net.URL

/**
 * @author Anton Akkuzin
 */
object IrzHttpClient {
    lateinit var authApi: AuthApi
    lateinit var usersApi: UsersApi
    lateinit var newsApi: NewsApi
    lateinit var imagesApi: ImagesApi
    lateinit var likesApi: LikesApi
    lateinit var userPositionsApi: UserPositionsApi
    lateinit var subscriptionsApi: SubscriptionsApi
    lateinit var positionsApi: PositionsApi
    lateinit var messengerApi: MessengerApi

    fun init(props: ClientProperties) {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        val retrofit = Retrofit.Builder()
            .baseUrl(URL(props.proto, props.host, props.port, "/"))
            .client(props.clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        authApi = retrofit.create(AuthApi::class.java)
        usersApi = retrofit.create(UsersApi::class.java)
        newsApi = retrofit.create(NewsApi::class.java)
        imagesApi = retrofit.create(ImagesApi::class.java)
        likesApi = retrofit.create(LikesApi::class.java)
        userPositionsApi = retrofit.create(UserPositionsApi::class.java)
        subscriptionsApi = retrofit.create(SubscriptionsApi::class.java)
        positionsApi = retrofit.create(PositionsApi::class.java)
        messengerApi = retrofit.create(MessengerApi::class.java)
    }
}
