package dev.nehal.insane.newd.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.DetailBottomSheetDialogFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences


class MainActivity1 : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    companion object {
        const val APP_UPDATE_REQ = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.nehal.insane.R.layout.activity_main1)
        val navView: BottomNavigationView = findViewById(dev.nehal.insane.R.id.nav_view)

        val navController = findNavController(dev.nehal.insane.R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setDataInPref()
        checkUpdate()
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


    private fun checkUpdate() {
        // Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(this)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    APP_UPDATE_REQ
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQ) {
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }

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
