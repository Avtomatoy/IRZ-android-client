package ru.avtomaton.irz.app.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.model.pojo.AuthBody
import ru.avtomaton.irz.app.model.pojo.JwtTokens
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Anton Akkuzin
 */
object CredentialsManager {

    private var tokens: AtomicReference<JwtTokens> = AtomicReference(null)

    private val email: Preferences.Key<String> = stringPreferencesKey("email")
    private val password: Preferences.Key<String> = stringPreferencesKey("password")

    private lateinit var dataStore: DataStore<Preferences>

    @Volatile
    private var authenticated: Boolean = false

    fun init(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    private suspend fun readCredentials(): AuthBody? {
        val preferences = dataStore.data.first()
        val email = preferences[email]
        val password = preferences[password]
        email ?: return null
        password ?: return null
        return AuthBody(email, password)
    }

    suspend fun login(): Boolean {
        val authBody = readCredentials() ?: return false
        return login(authBody)
    }

    suspend fun login(authBody: AuthBody): Boolean {
        val response = IrzClient.authApi.authenticate(authBody)
        authenticated = response.isSuccessful
        saveCredentials(authBody)
        return response.isSuccessful
    }

    private suspend fun saveCredentials(authBody: AuthBody) {
        dataStore.edit {
            it[email] = authBody.email
            it[password] = authBody.password
        }
    }

    suspend fun exit() {
        dropTokens()
        dataStore.edit { it.clear() }
    }

    fun isAuthenticated(): Boolean {
        return authenticated
    }

    fun saveTokens(jwtTokens: JwtTokens) {
        tokens.set(jwtTokens)
        authenticated = true
    }

    fun dropTokens() {
        tokens.set(null)
        authenticated = false
    }

    fun getTokens(): JwtTokens {
        return tokens.get()
    }
}
