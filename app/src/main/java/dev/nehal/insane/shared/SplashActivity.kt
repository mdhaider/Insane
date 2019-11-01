package dev.nehal.insane.shared

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.modules.MainActivity
import dev.nehal.insane.modules.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(auth.currentUser==null){

            intent = Intent(
                this,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()

        } else {
            intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}