package dev.nehal.insane.modules.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityLoginBinding
import dev.nehal.insane.modules.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser !=null){
            intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}
