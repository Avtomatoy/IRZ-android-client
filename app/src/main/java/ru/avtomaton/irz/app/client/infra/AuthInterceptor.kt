package ru.avtomaton.irz.app.client.infra

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.client.authentication_authenticate
import ru.avtomaton.irz.app.infra.SessionManager
import java.util.concurrent.atomic.AtomicReference

const val HTTP_UNAUTHORIZED = 401

/**
 * @author Anton Akkuzin
 */
class AuthInterceptor : Interceptor {

    private val gson: Gson = Gson()

    private val authRequest: AtomicReference<Request?> = AtomicReference(null)

    private var jwtTokens: AtomicReference<JwtTokens> =
        AtomicReference(JwtTokens("", ""))

    override fun intercept(chain: Chain): Response {
        if (isAuthRequest(chain)) {
            return interceptAuth(chain, chain.request())
        }
        if (!SessionManager.authenticated()) {
            return chain.proceed(chain.request())
        }
        val request = insertBearerHeader(chain.request())
        val response = chain.proceed(request)
        return retryIfNeeded(chain, response)
    }

    private fun insertBearerHeader(request: Request): Request {
        return request
            .newBuilder()
            .addHeader("Authorization", "Bearer ${jwtTokens.get().jwtToken}")
            .build()
    }

    private fun interceptAuth(chain: Chain, request: Request): Response {
        val response = chain.proceed(request)

        if (response.isSuccessful) {
            authRequest.set(response.request())
            val newJwtTokens =
                gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), JwtTokens::class.java)
            jwtTokens.set(newJwtTokens)
        }
        return response
    }

    private fun retryIfNeeded(chain: Chain, response: Response): Response {
        if (response.code() != HTTP_UNAUTHORIZED) {
            return response
        }
        println("Performing retry...")
        val authReq = authRequest.get() ?: return response
        val authResponse = interceptAuth(chain, authReq)
        if (authResponse.isSuccessful) {
            return chain.proceed(insertBearerHeader(chain.request()))
        }
        authRequest.set(null)
        return response
    }

    private fun isAuthRequest(chain: Chain): Boolean {
        return chain.request().url().toString().contains(authentication_authenticate)
    }
}