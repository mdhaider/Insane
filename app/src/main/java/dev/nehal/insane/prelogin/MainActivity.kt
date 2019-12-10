package dev.nehal.insane.prelogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import dev.nehal.insane.R
import dev.nehal.insane.model.Users
import dev.nehal.insane.navigation.RewardsDetailFragment
import dev.nehal.insane.postlogin.DetailBottomSheetDialogFragment
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import dev.nehal.insane.util.getCurrentNavigationFragment
import eu.dkaratzas.android.inapp.update.Constants
import eu.dkaratzas.android.inapp.update.InAppUpdateManager


class MainActivity : AppCompatActivity(), DetailBottomSheetDialogFragment.BottomSheetListener,
    RewardsDetailFragment.InterfaceComm {
    private lateinit var db: FirebaseFirestore
    private val REQ_CODE_VERSION_UPDATE = 530
    private val TAG = "MainActivity"
    private var inAppUpdateManager: InAppUpdateManager? = null
    private lateinit var navView: BottomNavigationView
    private lateinit var navGraph: NavGraph
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
            .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
            .mode(Constants.UpdateMode.IMMEDIATE)

        inAppUpdateManager!!.checkForAppUpdate()

        setDataInPref()
    }

    private fun getNext() {

        navView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        navGraph= navController.navInflater.inflate(R.navigation.mobile_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        //  setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        regToken()
    }

    fun showBottomSheet() {
        val bottomSheetFragment =
            DetailBottomSheetDialogFragment()
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

                getNext()

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

    override fun closeBtn(isRevealed: Boolean) {
        Log.d("valreveal", isRevealed.toString())
        if (true) {
            navGraph.setStartDestination(R.id.navigation_notifications);
        } else {
            navGraph.setStartDestination(R.id.navigation_notifications);
        }
        navController.setGraph(navGraph); }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
}
