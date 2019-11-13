package dev.nehal.insane.modules.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityLoginBinding
import dev.nehal.insane.shared.AppPreferences


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.login_nav_host) as NavHostFragment?
        val navController = navHostFragment!!.navController

        val state= AppPreferences.signUpState

        when(state){
            1 -> navController.navigate(R.id.action_a_to_b)
            2 -> navController.navigate(R.id.action_a_to_c)
        }
    }
}
