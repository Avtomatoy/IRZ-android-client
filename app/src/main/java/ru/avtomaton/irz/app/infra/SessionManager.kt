package ru.avtomaton.irz.app.infra

import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Anton Akkuzin
 */
object SessionManager {

    private val credentials : AtomicReference<AuthBody?> = AtomicReference(null)
    private var credentialsLoaded : Boolean = false

    fun getCredentials() : AuthBody? {
        return credentials.get()
    }

    fun dropCredentials() {
        credentials.set(null)
    }

    fun setCredentials(authBody: AuthBody?) {
        credentials.set(authBody)
    }

    fun isCredentialsLoaded() : Boolean {
        return credentialsLoaded
    }

    fun markCredentialsLoaded() {
        credentialsLoaded = true
    }
}
