package dev.nehal.insane.modules.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dev.nehal.insane.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var db: FirebaseFirestore? = null
    private var reference: StorageReference? = null
    private var TAG = "HomeFragment"
    private val PICK_IMAGE_REQUEST = 100
    private var filePath: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val btn: Button = root.findViewById(R.id.button)
        val et: EditText = root.findViewById(R.id.editText)

        btn.setOnClickListener {
            addContact(et.text.toString())
        }

        db = FirebaseFirestore.getInstance()
        reference = FirebaseStorage.getInstance().reference


        return root
    }

    private fun addContact(text: String) {
        val post = HashMap<String, Any>()
        post["comments"] = text
        post["image"] = "imgref"

        db!!.collection("post")
            .add(post)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
}