package ru.avtomaton.irz.app.client

import com.google.gson.Gson
import okhttp3.*
import okhttp3.Interceptor.Chain
import ru.avtomaton.irz.app.constants.AUTHENTICATION_AUTHENTICATE
import ru.avtomaton.irz.app.constants.AUTHENTICATION_REFRESH
import ru.avtomaton.irz.app.constants.HTTP_UNAUTHORIZED
import ru.avtomaton.irz.app.model.pojo.JwtTokens
import ru.avtomaton.irz.app.services.CredentialsManager
import java.net.URL

/**
 * @author Anton Akkuzin
 */
object AuthInterceptor : Interceptor {

    private val gson: Gson = Gson()
    private val mediaType: MediaType = MediaType.get("application/json; charset=UTF-8")

    override fun intercept(chain: Chain): Response {
        if (isAuthRequest(chain)) {
            return interceptAuth(chain, chain.request())
        }
        if (!CredentialsManager.isAuthenticated()) {
            return chain.proceed(chain.request())
        }
        val request = insertBearerHeader(chain.request())
        val response = chain.proceed(request)
        return retry(chain, response)
    }

    private fun insertBearerHeader(request: Request): Request {
        return request
            .newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${CredentialsManager.getTokens().jwtToken}"
            )
            .build()
    }

    private fun interceptAuth(chain: Chain, request: Request): Response {
        val response = chain.proceed(request)
        if (response.isSuccessful) {
            CredentialsManager.saveTokens(convert(response))
        }
        return response
    }

    private fun retry(chain: Chain, response: Response): Response {
        if (response.code() != HTTP_UNAUTHORIZED) {
            return response
        }
        response.close()
        val refreshRequest = refreshRequest(chain.request())
        val refreshResponse = interceptAuth(chain, refreshRequest)
        if (refreshResponse.isSuccessful) {
            return chain.proceed(insertBearerHeader(chain.request()))
        }
        CredentialsManager.dropTokens()
        return refreshResponse
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
            .post(RequestBody.create(mediaType, gson.toJson(CredentialsManager.getTokens())))
            .build()
    }

    private fun isAuthRequest(chain: Chain): Boolean {
        return chain.request().url().toString().contains(AUTHENTICATION_AUTHENTICATE)
    }

    private fun convert(response: Response): JwtTokens {
        return gson.fromJson(response.peekBody(Long.MAX_VALUE).string(), JwtTokens::class.java)
    }
}