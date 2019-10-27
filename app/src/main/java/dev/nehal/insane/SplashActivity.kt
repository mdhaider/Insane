package dev.nehal.insane

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.nehal.insane.modules.login.LoginActivity
import dev.nehal.insane.shared.Const

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getSharedPreferences(Const.PREF_NAME, Const.PRIVATE_MODE).getBoolean(
                Const.IS_LOGGED_IN,
                false
            )
        ) {
            intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        } else {
            intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }

    }
}