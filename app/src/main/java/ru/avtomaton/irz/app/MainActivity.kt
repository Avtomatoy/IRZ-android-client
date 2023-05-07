package ru.avtomaton.irz.app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import ru.avtomaton.irz.app.activity.NewsActivity
import ru.avtomaton.irz.app.client.AuthInterceptor
import ru.avtomaton.irz.app.client.ClientProperties
import ru.avtomaton.irz.app.client.IrzHttpClient
import ru.avtomaton.irz.app.client.IrzSignalRClientBuilder
import ru.avtomaton.irz.app.databinding.ActivityMainBinding
import ru.avtomaton.irz.app.services.CredentialsManager
import java.util.Properties

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * 1Az$dsdwdadqwdqds
 *
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clientProperties = Properties().let {
            it.load(resources.openRawResource(R.raw.application))
            ClientProperties(
                "http",
                it.getProperty("server.host"),
                it.getProperty("server.port").toInt(),
                OkHttpClient.Builder().addInterceptor(AuthInterceptor)
            )
        }
        IrzHttpClient.init(clientProperties)
        IrzSignalRClientBuilder.init(clientProperties)

        CredentialsManager.init(dataStore)
        this.lifecycleScope.launch {
            try {
                CredentialsManager.login()
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
            startActivity(Intent(this@MainActivity, NewsActivity::class.java))
        }
    }
}
