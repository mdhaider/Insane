package dev.nehal.insane.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentAddPhotoBinding
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import dev.nehal.insane.shared.hideKeyboard
import kotlinx.android.synthetic.main.fragment_add_photo.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoFragment : Fragment() {
    var photoUri: Uri? = null
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var filePath: Uri? = null
    private lateinit var binding: FragmentAddPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_photo, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSharedIntent()

        binding.rootView.visibility=View.GONE

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        addphoto_btn_upload.setOnClickListener {
            hideKeyboard()
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            var list: ArrayList<Image> = ArrayList<Image>()
            list = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            Log.d("TAG", list.toString())

            binding.rootView.visibility=View.VISIBLE

            Glide.with(this).load(list[0].path).into(addphoto_image)
            Log.d("path", list[0].path.toString())
            rootView.visibility = View.VISIBLE

            photoUri = Uri.fromFile(File(list[0].path))

        } else {
            findNavController().navigate(R.id.action_photo_home)

        }
    }

    private fun contentUpload() {
        progress_bar.visibility = View.VISIBLE
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = timeStamp + ".png"
        val storageRef = storage?.reference?.child("uploadedImages")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot ->
            progress_bar.visibility = View.GONE

            Toast.makeText(
                activity, getString(dev.nehal.insane.R.string.upload_success),
                Toast.LENGTH_SHORT
            ).show()


            val user = ModelPreferences(activity!!.application).getObject(Const.PROF_USER, Users::class.java)

            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val url = uri.toString()
                    val contentDTO = ContentDTO()
                    contentDTO.uid = user?.userUID
                    contentDTO.imgUrl = url
                    contentDTO.imgCaption = addphoto_edit_explain.text.toString()
                    contentDTO.imgUploadDate = System.currentTimeMillis()
                    firestore?.collection("uploadedImages")?.document()?.set(contentDTO)

                    activity!!.setResult(Activity.RESULT_OK)
                    findNavController().navigate(R.id.action_photo_home)

                }
                .addOnFailureListener {
                    progress_bar.visibility = View.GONE

                    Toast.makeText(
                        activity, getString(dev.nehal.insane.R.string.upload_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun onSharedIntent() {
        val receiverdIntent = activity!!.intent
        val receivedAction = receiverdIntent.action
        val receivedType = receiverdIntent.type

        if (receivedAction == Intent.ACTION_SEND) {

            // check mime type
            if (receivedType!!.startsWith("text/")) {

                val receivedText = receiverdIntent
                    .getStringExtra(Intent.EXTRA_TEXT)
                if (receivedText != null) {
                    //do your stuff
                }
            } else if (receivedType.startsWith("image/")) {

                val receiveUri = receiverdIntent
                    .getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri

                if (receiveUri != null) {
                    //do your stuff
                    photoUri = receiveUri// save to your own Uri object

                    Log.e("Add", receiveUri.toString())

                    addphoto_image.setImageURI(photoUri)
                }
            }

        } else {
            startImagePicker()
        }

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
}
