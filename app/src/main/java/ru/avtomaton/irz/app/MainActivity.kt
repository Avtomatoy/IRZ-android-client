package ru.avtomaton.irz.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.avtomaton.irz.app.client.IrzClient
import ru.avtomaton.irz.app.client.infra.CredentialsHolder

/**
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val irzClient = IrzClient(
            "https://10.0.2.2:7116/api/",
            CredentialsHolder("user@example.com", "string")
        )
        irzClient.authenticate()
    }
}