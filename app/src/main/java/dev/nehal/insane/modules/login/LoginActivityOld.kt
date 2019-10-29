package dev.nehal.insane.modules.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityLoginOldBinding

class LoginActivityOld : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOldBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_old)
    }
}
