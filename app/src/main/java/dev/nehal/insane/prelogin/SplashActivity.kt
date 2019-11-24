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
import dev.nehal.insane.newd.main.MainActivity1
import dev.nehal.insane.shared.AppPreferences
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val splashTime = 1500L // 3 seconds
    private lateinit var myHandler : Handler
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)

        myHandler = Handler()
        binding.tvSalut.text=getGreetingMessage()
        binding.tvSalut1.text=AppPreferences.userName

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

    fun getGreetingMessage():String{
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