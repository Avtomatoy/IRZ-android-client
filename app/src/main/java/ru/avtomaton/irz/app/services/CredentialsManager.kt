package ru.avtomaton.irz.app.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.model.pojo.Credentials
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

    private suspend fun readCredentials(): Credentials? {
        return try {
            val preferences = dataStore.data.first()
            val email = preferences[email]
            val password = preferences[password]
            email ?: return null
            password ?: return null
            Credentials(email, password)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            null
        }
    }

    suspend fun login(): Boolean {
        val authBody = readCredentials() ?: return false
        return login(authBody)
    }

    suspend fun login(credentials: Credentials): Boolean {
        val response = IrzHttpClient.authApi.authenticate(credentials)
        authenticated = response.isSuccessful
        
        saveCredentials(credentials)
        return response.isSuccessful
    }

    private suspend fun saveCredentials(credentials: Credentials) {
        dataStore.edit {
            it[email] = credentials.email
            it[password] = credentials.password
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
