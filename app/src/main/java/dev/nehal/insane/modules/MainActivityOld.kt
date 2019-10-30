package dev.nehal.insane.modules

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dev.nehal.insane.R
import dev.nehal.insane.shared.Const
import kotlinx.android.synthetic.main.activity_main_old.*

class MainActivityOld : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_old)
        setSupportActionBar(mainToolbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_liked_post,
               R.id.navigation_new_post,
               R.id.navigation_all_post,
               R.id.navigation_others
            )
        )
      //  setupActionBarWithNavController(navController, appBarConfiguration)
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
            val pho = user.phoneNumber
            val uid = user.uid

            getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString(Const.PHONE_NUM, pho?.takeLast(10))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signOut -> {
                FirebaseAuth.getInstance().signOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
