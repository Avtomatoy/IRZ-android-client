package ru.avtomaton.irz.app.client.infra

import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.JwtTokens

/**
 * @author Anton Akkuzin
 */
class CredentialsHolder(email: String, password: String) {

    private var tokens: JwtTokens = JwtTokens("", "")
    private val credentials : AuthBody = AuthBody(email, password)

    fun getCredentials(): AuthBody {
        return credentials
    }

    fun getTokens(): JwtTokens {
        return tokens
    }

    fun setTokens(authResponse: JwtTokens) {
        this.tokens = authResponse
    }
}