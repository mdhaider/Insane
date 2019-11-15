package dev.nehal.insane.modules

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.navigation.HomeTabActivity
import dev.nehal.insane.navigation.PeopleFragment
import dev.nehal.insane.navigation.TabActivity
import dev.nehal.insane.navigation.UserFragment
import dev.nehal.insane.prelogin.EnterMobileFragment
import dev.nehal.insane.shared.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val PICK_PROFILE_FROM_ALBUM = 10
    val APP_UPDATE_REQ = 101
    private lateinit var phone: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val id=intent.getIntExtra("id",1)

        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)


        // 앨범 접근 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        when(id){
            1 ->  bottom_navigation.selectedItemId=R.id.action_search
            2 ->  bottom_navigation.selectedItemId=R.id.action_add_photo
            3 ->  bottom_navigation.selectedItemId=R.id.action_favorite_alarm
            4 ->  bottom_navigation.selectedItemId=R.id.action_account
        }

        registerPushToken()
        getUserDetails()
        checkUpdate()
    }

    fun registerPushToken() {
        var pushToken = FirebaseInstanceId.getInstance().token
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var map = mutableMapOf<String, Any>()
        map["pushtoken"] = pushToken!!
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                startActivity(Intent(this, HomeTabActivity::class.java))
                finish()
                return true
            }
            R.id.action_search -> {
                val peopleFragment = PeopleFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, peopleFragment)
                    .commit()
                return true
            }

            R.id.action_favorite_alarm -> {
                startActivity(Intent(this, TabActivity::class.java))
                return true
            }
            R.id.action_account -> {
                val userFragment = UserFragment()
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val bundle = Bundle()
                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, userFragment)
                    .commit()
                return true
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 앨범에서 Profile Image 사진 선택시 호출 되는 부분분
        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            var imageUri = data?.data


            val uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
            //사진을 업로드 하는 부분  userProfileImages 폴더에 uid에 파일을 업로드함

            val ref: StorageReference = FirebaseStorage
                .getInstance()
                .reference
                .child("userProfileImages")
                .child(uid)

            ref.putFile(imageUri!!)
                .addOnCompleteListener { task ->

                    ref.downloadUrl
                        .addOnSuccessListener { uri ->
                            val url = uri.toString()
                            val map = HashMap<String, Any>()
                            map["image"] = url
                            FirebaseFirestore.getInstance().collection("profileImages")
                                .document(uid).set(map)
                            setProfileImage(url)
                        }
                }
        } else if (requestCode == APP_UPDATE_REQ) {
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }

    }

    private fun setProfileImage(url: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {

            db.collection("signup")
                .document(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.takeLast(10))
                .update("imageuri", url)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "UID failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "UID failed")
        }
    }

    private fun getProfileDetails() {

        val ph = FirebaseAuth.getInstance().currentUser?.phoneNumber
        phone = ph!!.takeLast(10)
        val dbRef = FirebaseFirestore.getInstance().collection("users").document(phone)
        dbRef.get()
            .addOnSuccessListener { document ->

                if (document.data != null) {
                    Log.d("Doc data", "DocumentSnapshot data: ${document.data}")
                    AppPreferences.isAdmin = document.getBoolean("admin")!!

                }

            }.addOnFailureListener { exception ->

                Log.d(EnterMobileFragment.TAG, exception.toString())
            }
    }

    private fun getUserDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val uid = user.uid

            Log.d("details", name + uid)
        }
    }

    override fun onBackPressed() {

        super.onBackPressed()

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
}
