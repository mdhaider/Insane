package dev.nehal.insane.modules

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.shared.Const

class MainActivityOld : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.nehal.insane.R.layout.activity_main_old)
        val navView: BottomNavigationView = findViewById(dev.nehal.insane.R.id.nav_view)

        val navController = findNavController(dev.nehal.insane.R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                dev.nehal.insane.R.id.navigation_home,
                dev.nehal.insane.R.id.navigation_liked_post,
                dev.nehal.insane.R.id.navigation_new_post,
                dev.nehal.insane.R.id.navigation_all_post,
                dev.nehal.insane.R.id.navigation_others
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val pref = getSharedPreferences(Const.PREF_NAME, Const.PRIVATE_MODE)
        val editor = pref.edit()
        editor.putBoolean(Const.IS_LOGGED_IN, true)
        editor.apply()

        getUser()

    }

   private fun getUser() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val pho=user.phoneNumber
            val uid = user.uid

            getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE).edit().putString(Const.PHONE_NUM, pho?.takeLast(10))
        }
    }
}
