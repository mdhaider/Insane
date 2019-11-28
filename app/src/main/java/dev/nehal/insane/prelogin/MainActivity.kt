package dev.nehal.insane.prelogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.DetailBottomSheetDialogFragment
import dev.nehal.insane.newd.main.getCurrentNavigationFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import eu.dkaratzas.android.inapp.update.Constants
import eu.dkaratzas.android.inapp.update.InAppUpdateManager


class MainActivity : AppCompatActivity(), DetailBottomSheetDialogFragment.BottomSheetListener {
    private lateinit var db: FirebaseFirestore
    private val REQ_CODE_VERSION_UPDATE = 530
    private val TAG = "MainActivity"
    private var inAppUpdateManager: InAppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.nehal.insane.R.layout.activity_main)

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
            .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
            .mode(Constants.UpdateMode.IMMEDIATE)

        inAppUpdateManager!!.checkForAppUpdate()

        val navView: BottomNavigationView = findViewById(dev.nehal.insane.R.id.nav_view)

        val navController = findNavController(dev.nehal.insane.R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setDataInPref()

        regToken()

    }

    fun showBottomSheet() {
        val bottomSheetFragment = DetailBottomSheetDialogFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun setDataInPref() {
        db = FirebaseFirestore.getInstance()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // If the update is cancelled by the user,
                // you can request to start the update again.
                inAppUpdateManager!!.checkForAppUpdate()

                Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    override fun onOptionClick(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        got()
    }

    private fun got() {
        val currentFragment = supportFragmentManager.getCurrentNavigationFragment()

        this.supportFragmentManager.findFragmentByTag("tag")

        Log.d("gtr", currentFragment.toString())
    }

    private fun regToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val pushToken = instanceIdResult.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = pushToken
            FirebaseFirestore.getInstance().collection("pushTokens").document(uid!!).set(map)

        }
            .addOnFailureListener { exception ->
                Log.d("Not registered", "token")

            }
    }
}
