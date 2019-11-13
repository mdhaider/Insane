package dev.nehal.insane.prelogin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.LoginActivity
import dev.nehal.insane.newd.main.MainActivity1

class SplashActivity : AppCompatActivity() {
    private val splashTime = 1500L // 3 seconds
    private lateinit var myHandler : Handler
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        myHandler = Handler()

        myHandler.postDelayed({
           decisionToGo()
        },splashTime)
    }

    private fun decisionToGo() {
        if (auth.currentUser == null) {
            intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()

        } else {
            intent = Intent(
                this,
                MainActivity1::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}