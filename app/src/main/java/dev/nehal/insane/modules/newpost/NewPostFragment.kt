package dev.nehal.insane.modules.newpost

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import dev.nehal.insane.R
import dev.nehal.insane.databinding.FragmentNewPostBinding
import java.io.File


class NewPostFragment : Fragment() {

    private lateinit var newPostViewModel: NewPostViewModel
    private lateinit var binding: FragmentNewPostBinding
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private lateinit var progressbar: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newPostViewModel =
            ViewModelProviders.of(this).get(NewPostViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_new_post, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            .setShowCamera(false)                //  Show camera button
            .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
            .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
            .setDoneTitle("Done")               //  Done button title
            .setLimitMessage("You have reached selection limit")    // Selection limit message
            .setMaxSize(1)                     //  Max images can be selected
            .setSavePath("ImagePicker")         //  Image capture folder name
            .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
            .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
            .setKeepScreenOn(true)              //  Keep screen on when selecting images
            .start()                          //  Start ImagePicker
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            var list: ArrayList<Image> = ArrayList<Image>()
            list = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            Log.d("TAG", list.toString())

            Glide.with(this).load(list[0].path).into(binding.rv)
            Log.d("path", list[0].path.toString())

            // uploadImage()

            filePath = Uri.fromFile(File(list[0].path))
           uploadFile()

         //   uploadImage()

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun uploadFile() {

        progressbar = ProgressDialog(activity).apply {
            setTitle("Uploading Picture....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()

            val storageRef = FirebaseStorage.getInstance().reference

            val riversRef = storageRef.child("images/${filePath?.lastPathSegment}")
            var uploadTask = riversRef.putFile(filePath!!)
            var value = 0.0

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                progressbar.dismiss()
            }.addOnSuccessListener {


                riversRef.downloadUrl.addOnSuccessListener {uri->
                    Log.d("TAG", "onSuccess: uri= "+ uri)
                    addUploadRecordToDb(uri.toString())
                }
                progressbar.dismiss()
            }.addOnProgressListener { taskSnapshot ->
                value = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                Log.v("value", "value==" + value)
                progressbar.setMessage("Uploaded.. " + value.toInt() + "%")

            }

        }
    }

    private fun addUploadRecordToDb(uri: String){
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.collection("posts")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(activity, "Saved to DB", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(activity, "Error saving to DB", Toast.LENGTH_LONG).show()

            }

    }
}