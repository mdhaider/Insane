package dev.nehal.insane.newd.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.DetailBottomSheetDialogFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences

class MainActivity1 : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setDataInPref()
    }

     fun showBottomSheet(){
        val bottomSheetFragment = DetailBottomSheetDialogFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun setDataInPref() {
        db= FirebaseFirestore.getInstance()
        val dbRef = db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)

        dbRef.get()
            .addOnSuccessListener { document ->
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    ModelPreferences(application).putObject(Const.PROF_USER, users)
                }

            }.addOnFailureListener { exception ->

                Log.d("MainActivity", exception.toString())
            }
    }
}
