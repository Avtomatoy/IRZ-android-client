package ru.avtomaton.irz.app.client.infra

import com.google.gson.Gson
import okhttp3.*
import okhttp3.Interceptor.Chain
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens
import ru.avtomaton.irz.app.constants.AUTHENTICATION_AUTHENTICATE
import ru.avtomaton.irz.app.constants.AUTHENTICATION_REFRESH
import ru.avtomaton.irz.app.constants.HTTP_UNAUTHORIZED
import ru.avtomaton.irz.app.infra.SessionManager
import java.net.URL
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Anton Akkuzin
 */
class AuthInterceptor : Interceptor {

    private val gson: Gson = Gson()
    private val emptyTokens: JwtTokens = JwtTokens("", "")
    private var tokens: AtomicReference<JwtTokens> = AtomicReference(emptyTokens)

    override fun intercept(chain: Chain): Response {
        if (isAuthRequest(chain)) {
            return interceptAuth(chain, chain.request())
        }
        if (!SessionManager.authenticated()) {
            return chain.proceed(chain.request())
        }
        val request = insertBearerHeader(chain.request())
        val response = chain.proceed(request)
        return retry(chain, response)
    }

    private fun insertBearerHeader(request: Request): Request {
        return request
            .newBuilder()
            .addHeader("Authorization", "Bearer ${tokens.get().jwtToken}")
            .build()
    }

    private fun interceptAuth(chain: Chain, request: Request): Response {
        val response = chain.proceed(request)
        if (response.isSuccessful) {
            tokens.set(
                gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), JwtTokens::class.java)
            )
        }
        return response
    }

    private fun retry(chain: Chain, response: Response): Response {
        if (response.code() != HTTP_UNAUTHORIZED) {
            return response
        }
        println("performing retry")
        val refreshRequest = refreshRequest(chain.request())
        val refreshResponse = interceptAuth(chain, refreshRequest)
        if (refreshResponse.isSuccessful) {
            println("retry successful")
            return chain.proceed(insertBearerHeader(chain.request()))
        }
        tokens.set(emptyTokens)
        return response
    }

    private fun refreshRequest(request: Request): Request {
        val originUrl = request.url().url()
        val url = URL(
            originUrl.protocol,
            originUrl.host,
            originUrl.port,
            AUTHENTICATION_REFRESH
        )
        return Request.Builder()
            .url(url)
            .post(
                RequestBody.create(
                    MediaType.parse("application/json"),
                    gson.toJson(tokens)
                )
            )
            .build()
    }

    private fun isAuthRequest(chain: Chain): Boolean {
        return chain.request().url().toString().contains(AUTHENTICATION_AUTHENTICATE)
    }
}