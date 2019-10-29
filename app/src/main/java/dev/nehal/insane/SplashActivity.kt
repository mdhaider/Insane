package dev.nehal.insane

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.modules.MainActivityOld
import dev.nehal.insane.modules.login.LoginActivityOld

class SplashActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(auth.currentUser==null){

            intent = Intent(
                this,
                LoginActivityOld::class.java
            )
            startActivity(intent)
            finish()

        } else {
            intent = Intent(
                this,
                MainActivityOld::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}