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
            "http://10.0.2.2:5249/api/",
            CredentialsHolder("user@example.com", "string")
        )
    }
}