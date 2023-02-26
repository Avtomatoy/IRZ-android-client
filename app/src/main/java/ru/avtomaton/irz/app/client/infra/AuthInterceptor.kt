package ru.avtomaton.irz.app.client.infra

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.client.authentication_authenticate
import java.util.Objects
import java.util.concurrent.atomic.AtomicReference

const val HTTP_UNAUTHORIZED = 401

/**
 * @author Anton Akkuzin
 */
class AuthInterceptor : Interceptor {

    private val gson : Gson = Gson()

    private val authRequest: AtomicReference<Request?> =
        AtomicReference(null)

    private var jwtTokens: AtomicReference<JwtTokens> =
        AtomicReference(JwtTokens("", ""))

    override fun intercept(chain: Interceptor.Chain): Response {
        if (isAuthRequest(chain)) {
            return interceptAuth(chain, chain.request())
        }
        val request = insertBearerHeader(chain.request())

        val response = chain.proceed(request)
        if (response.code() == HTTP_UNAUTHORIZED) {
            println("UNAUTHORIZED")
            val authReq = authRequest.get()
            if (Objects.isNull(authReq)) {
                return response
            }
            println("Making re-auth request...")
            val reAuthResponse = interceptAuth(chain, authReq!!)
            if (reAuthResponse.isSuccessful) {
                return chain.proceed(insertBearerHeader(chain.request()))
            } else {
                authRequest.compareAndSet(authRequest.get(), null)
            }
        }
        return response
    }

    private fun insertBearerHeader(request: Request) : Request {
        return request
            .newBuilder()
            .addHeader("Authorization", "Bearer ${jwtTokens.get().jwtToken}")
            .build()
    }

    private fun interceptAuth(chain: Interceptor.Chain, request: Request) : Response {
        val response = chain.proceed(request)
        if (response.isSuccessful) {
            authRequest.compareAndSet(this.authRequest.get(), response.request())

            val newJwtTokens = gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), JwtTokens::class.java)
            jwtTokens.compareAndSet(this.jwtTokens.get(), newJwtTokens)
        }
        return response
    }

    private fun isAuthRequest(chain: Interceptor.Chain): Boolean {
        return chain.request().url().toString().contains(authentication_authenticate)
    }
}