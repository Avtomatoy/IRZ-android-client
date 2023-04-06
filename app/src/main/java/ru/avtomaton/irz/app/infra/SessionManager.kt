package ru.avtomaton.irz.app.infra

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.avtomaton.irz.app.client.api.auth.AuthRepository
import ru.avtomaton.irz.app.client.api.auth.models.AuthBody
import java.util.concurrent.atomic.AtomicReference

/**
 * @author Anton Akkuzin
 */
object SessionManager {

    private val email : Preferences.Key<String> = stringPreferencesKey("email")
    private val password : Preferences.Key<String> = stringPreferencesKey("password")

    private val credentials : AtomicReference<AuthBody?> = AtomicReference(null)
    private var initialized : Boolean = false
    private lateinit var dataStore: DataStore<Preferences>


    suspend fun login() : Boolean {
        if (!initialized) {
            throw java.lang.IllegalStateException("not initialized")
        }
        val preferences = dataStore.data.first()
        val email = preferences[email]
        val password = preferences[password]
        email ?: return false
        password ?: return false
        val authBody = AuthBody(email, password)
        credentials.set(authBody)
        val result = AuthRepository.auth(authBody)

        val authenticated = result.isSuccess && result.getOrNull()!!
        if (!authenticated) {
            dropCredentials()
        }
        return authenticated
    }

    fun init(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
        initialized = true
    }

    suspend fun setCredentials(authBody: AuthBody) {
        credentials.set(authBody)
        dataStore.edit { settings ->
            settings[email] = authBody.email
            settings[password] = authBody.password
        }
    }

    suspend fun dropCredentials() {
        credentials.set(null)
        dataStore.edit { it.clear() }
    }

    fun getCredentials() : AuthBody? {
        return credentials.get()
    }

    fun authenticated() : Boolean {
        return credentials.get() != null
    }
}
