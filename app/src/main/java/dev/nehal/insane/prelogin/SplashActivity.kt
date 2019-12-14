package dev.nehal.insane.prelogin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivitySplashBinding
import dev.nehal.insane.modules.login.LoginActivity
import dev.nehal.insane.shared.AppPreferences
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashBinding
    private val splashTime = 1000L // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        binding.tvSalut.text = getGreetingMessage()
        binding.tvSalut1.text = AppPreferences.userName

        Handler().postDelayed({
            decisionToGo()
        }, splashTime)

    }

    private fun decisionToGo() {
        intent = if (auth.currentUser == null) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

        return when (timeOfDay) {
            in 0..11 -> "Good Morning"
            in 12..15 -> "Good Afternoon"
            in 16..20 -> "Good Evening"
            in 21..23 -> "Good Night"
            else -> {
                "Hello"
            }
        }
    }
}