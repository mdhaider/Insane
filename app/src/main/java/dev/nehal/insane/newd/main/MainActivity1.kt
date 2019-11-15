package dev.nehal.insane.newd.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.nehal.insane.R
import dev.nehal.insane.navigation.DetailBottomSheetDialogFragment

class MainActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

     fun showBottomSheet(){
        val bottomSheetFragment = DetailBottomSheetDialogFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
}
