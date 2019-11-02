package dev.nehal.insane.modules

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.EnterMobileFragment
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.navigation.*
import dev.nehal.insane.shared.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    val PICK_PROFILE_FROM_ALBUM = 10
    private lateinit var phone: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress_bar.visibility = View.VISIBLE

        // Bottom Navigation View
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.selectedItemId = R.id.action_home

        // 앨범 접근 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        //푸시토큰 서버 등록
        registerPushToken()
        getUserDetails()
    }

    fun registerPushToken() {
        var pushToken = FirebaseInstanceId.getInstance().token
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var map = mutableMapOf<String, Any>()
        map["pushtoken"] = pushToken!!
        FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
    }

    fun setToolbarDefault() {
        toolbar_title_image.visibility = View.VISIBLE
        toolbar_btn_back.visibility = View.GONE
        toolbar_username.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when (item.itemId) {
            R.id.action_home -> {

                val detailViewFragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, detailViewFragment)
                    .commit()
                return true
            }
            R.id.action_search -> {
                val gridFragment = GridFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, gridFragment)
                    .commit()
                return true
            }
            R.id.action_add_photo -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                } else {
                    Toast.makeText(
                        this,
                        "You do not have permission to read storage.",
                        Toast.LENGTH_LONG
                    ).show()
                }
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
        }

    }

    private fun setProfileImage(url:String){
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            try {

                db.collection("signup").document(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!.takeLast(10)).update("imageuri",url)
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

            Log.d("details", name+uid)
        }
    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.main_content)
        (fragment as? IOnBackPressed)?.onBackPressed()?.not()?.let {
            super.onBackPressed()
        }
    }
}
