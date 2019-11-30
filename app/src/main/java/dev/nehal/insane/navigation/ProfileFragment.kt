package dev.nehal.insane.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ProfileFragmentBinding
import dev.nehal.insane.model.Users
import dev.nehal.insane.modules.login.VerifyPhoneFragment
import dev.nehal.insane.shared.hideKeyboard
import java.io.File
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var uid: String
    private lateinit var userName: String
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        uid = FirebaseAuth.getInstance().currentUser!!.uid

        binding.profCancel.setOnClickListener {
            findNavController().navigate(R.id.action_profile_home_profile)
        }

        // Profile Image Click Listener
        binding.profChange.setOnClickListener {
            startImagePicker()
        }

        binding.profSave.setOnClickListener {
            if (binding.profName.text.toString() != userName) {
                binding.profProgress.visibility=View.VISIBLE
                saveDataChange(binding.profName.text.toString())
                hideKeyboard()
            } else{
                findNavController().navigate(R.id.action_profile_home_profile)
            }
        }

        getData()
    }

    private fun getData() {
        val dbRef = db.collection("users").document(uid)

        binding.llParent.visibility = View.GONE
        binding.profProgress.visibility = View.VISIBLE

        dbRef.get()
            .addOnSuccessListener { document ->
                binding.profProgress.visibility = View.GONE
                binding.llParent.visibility = View.VISIBLE
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    setDataUI(users)
                    userName = users.userName
                }

            }.addOnFailureListener { exception ->
                binding.profProgress.visibility = View.GONE
                binding.llParent.visibility = View.VISIBLE
                Log.d("ProfileFragment", exception.toString())
            }
    }

    private fun setDataUI(users: Users) {
        binding.profName.setText(users.userName)
        binding.profPhone.setText(users.phoneNumber)
        binding.profPhone.isEnabled = false

        setProfileImage(users.profImageUri)
    }

    private fun saveDataChange(userName: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {
            db.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("userName", userName)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                    binding.profProgress.visibility=View.GONE
                    findNavController().navigate(R.id.action_profile_home_profile)
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "prof failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "prof failed")
        }
    }

    private fun setProfileImage(url: String) {
        binding.profChange.text="Change profile picture"
        Glide.with(activity!!)
            .load(url)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .apply(RequestOptions().circleCrop())
            .into(binding.imgProf!!)
    }

    private fun startImagePicker() {
        ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
            .setToolbarColor("#212121")         //  Toolbar color
            .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
            .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
            .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
            .setProgressBarColor("#4CAF50")     //  ProgressBar color
            .setBackgroundColor("#212121")      //  Background color
            .setCameraOnly(false)               //  Camera mode
            .setMultipleMode(false)              //  Select multiple images or single image
            .setFolderMode(true)                //  Folder mode
            //  If the picker should include Videos or only Image Assets
            .setShowCamera(true)                //  Show camera button
            .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
            .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
            .setDoneTitle("Done")               //  Done button title
            .setLimitMessage("You have reached selection limit")    // Selection limit message
            .setMaxSize(1)
            .setSavePath("ImagePicker")         //  Image capture folder name
            .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
            .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
            .setKeepScreenOn(true)              //  Keep screen on when selecting images
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            var list: ArrayList<Image> = ArrayList<Image>()
            list = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            Log.d("TAG", list.toString())
            binding.profChange.text="Updating ...."

            photoUri = Uri.fromFile(File(list[0].path))

            setProfileImageToStorage(photoUri!!)
        } else{

        }
    }

    private fun setProfileImageToStorage(url: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref: StorageReference = FirebaseStorage
            .getInstance()
            .reference
            .child("userProfileImages")
            .child(uid)

        ref.putFile(url)
            .addOnCompleteListener { task ->

                ref.downloadUrl
                    .addOnSuccessListener { uri ->
                        val url = uri.toString()
                        val map = HashMap<String, Any>()
                        map["image"] = url
                        FirebaseFirestore.getInstance().collection("profileImages")
                            .document(uid).set(map)
                        setProfileImageToUser(url)
                    }
            }
    }

    private fun setProfileImageToUser(url: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        try {

            db.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .update("profImageUri", url)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with UID: $documentReference")
                   setProfileImage(url)
                }.addOnFailureListener { e ->
                    Log.d(VerifyPhoneFragment.TAG, "UID failed")
                }
        } catch (e: Exception) {
            Log.d(VerifyPhoneFragment.TAG, "UID failed")
        }
    }
}
