package ru.avtomaton.irz.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import ru.avtomaton.irz.app.activity.AuthActivity

/**
 * @author Anton Akkuzin
 */
class MainActivity : AppCompatActivity() {

    private val tag : String = "[Main]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.auth_btn).setOnClickListener {
            Log.i(tag, "Auth btn pressed")
            Toast.makeText(this, "Переходим к авторизации...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AuthActivity::class.java))
        }

        Log.i(tag, "onCreate()")
    }

    override fun onResume() {
        super.onResume()
        Log.i(tag, "onResume()")
    }

}