package dev.nehal.insane.modules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.EnterMobileFragment
import dev.nehal.insane.modules.login.LoginActivityOld
import dev.nehal.insane.shared.AppPreferences
import dev.nehal.insane.shared.Const
import kotlinx.android.synthetic.main.activity_main_old.*

class MainActivityOld : AppCompatActivity() {

    private lateinit var db:FirebaseFirestore
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

            getProfileDetails(pho!!.takeLast(10))

            getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString(Const.PHONE_NUM, pho?.takeLast(10))
        }
    }

    private fun getProfileDetails(phone:String){
        db= FirebaseFirestore.getInstance()

        val dbRef = db.collection("users").document(phone)
        dbRef.get()
            .addOnSuccessListener { document ->

                if (document.data != null) {
                    Log.d("Doc data", "DocumentSnapshot data: ${document.data}")
                    AppPreferences.isAdmin=document.getBoolean("admin")!!
                }

            }.addOnFailureListener { exception ->

                Log.d(EnterMobileFragment.TAG, exception.toString())
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
                showLoginActivity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoginActivity() {
        startActivity(
            Intent(this, LoginActivityOld::class.java)
        )
        finish()
    }

}
