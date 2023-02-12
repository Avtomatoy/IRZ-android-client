package ru.avtomaton.irz.app.client.infra

import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import ru.avtomaton.irz.app.client.api.auth.models.AuthResponse

/**
 * @author Anton Akkuzin
 */
class CredentialsHolder(email: String, password: String) {

    private var tokens: AuthResponse = AuthResponse("", "")
    private val credentials : AuthBody = AuthBody(email, password)

    fun getCredentials(): AuthBody {
        return credentials
    }

    fun getTokens(): AuthResponse {
        return tokens
    }

    fun setTokens(authResponse: AuthResponse) {
        this.tokens = authResponse
    }
}