package ru.avtomaton.irz.app.client.infra

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Anton Akkuzin
 */
class AuthInterceptor(private val sessionHolder: CredentialsHolder) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${sessionHolder.getTokens().jwtToken}")

        return chain.proceed(builder.build())
    }
}