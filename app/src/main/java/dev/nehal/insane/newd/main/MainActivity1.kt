package dev.nehal.insane.newd.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.navigation.DetailBottomSheetDialogFragment

class MainActivity1 : AppCompatActivity() {
    val PICK_PROFILE_FROM_ALBUM = 10
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            var imageUri = data?.data

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
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
                          //  setProfileImage(url)
                             sendProfileUpdate(url)
                        }
                }
        }

    }

    private fun sendProfileUpdate(url: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            db.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("profImageUri", url)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "prof failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "prof failed")
        }
    }
}
