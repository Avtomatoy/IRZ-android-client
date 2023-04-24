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
import ru.avtomaton.irz.app.activity.NewsActivity
import ru.avtomaton.irz.app.client.IrzClient
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

        val props = Properties()
        props.load(resources.openRawResource(R.raw.application))
        IrzClient.init(props)

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
