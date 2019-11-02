package dev.nehal.insane.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dev.nehal.insane.model.ContentDTO
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity : AppCompatActivity(), ActionBottomDialogFragment.ItemClickListener {

    val PICK_IMAGE_FROM_ALBUM = 0

    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    private lateinit var fileUri: Uri
    private lateinit var photoPickerIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(dev.nehal.insane.R.layout.activity_add_photo)

        onSharedIntent()


        // Firebase storage
        storage = FirebaseStorage.getInstance()
        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()


        addphoto_image.setOnClickListener {
            /* val photoPickerIntent = Intent(Intent.ACTION_PICK)
             photoPickerIntent.type = "image/*"
             startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)*/


             */

            showBottomSheet(addphoto_image)
        }

        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    fun showBottomSheet(view: View) {
        val addPhotoBottomDialogFragment = ActionBottomDialogFragment.newInstance()
        addPhotoBottomDialogFragment.show(
            supportFragmentManager,
            ActionBottomDialogFragment.TAG
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            //이미지 선택시
            if (resultCode == Activity.RESULT_OK) {
                //이미지뷰에 이미지 세팅
                println(data?.data)
                photoUri = data?.data
                addphoto_image.setImageURI(data?.data)
            } else {
                finish()
            }

        }
    }

    fun contentUpload() {
        progress_bar.visibility = View.VISIBLE

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot ->
            progress_bar.visibility = View.GONE

            Toast.makeText(
                this, getString(dev.nehal.insane.R.string.upload_success),
                Toast.LENGTH_SHORT
            ).show()


            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val url = uri.toString()
                    //시간 생성
                    val contentDTO = ContentDTO()

                    //이미지 주소
                    contentDTO.imageUrl = url.toString()
                    //유저의 UID
                    contentDTO.uid = auth?.currentUser?.uid
                    contentDTO.userName = auth?.currentUser?.displayName
                    //게시물의 설명
                    contentDTO.explain = addphoto_edit_explain.text.toString()
                    //유저의 아이디
                    contentDTO.userId = auth?.currentUser?.phoneNumber
                    //게시물 업로드 시간
                    contentDTO.timestamp = System.currentTimeMillis()

                    //게시물을 데이터를 생성 및 엑티비티 종료
                    firestore?.collection("images")?.document()?.set(contentDTO)

                    setResult(Activity.RESULT_OK)
                    finish()


                }
                .addOnFailureListener {
                    progress_bar.visibility = View.GONE

                    Toast.makeText(
                        this, getString(dev.nehal.insane.R.string.upload_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun onSharedIntent() {
        val receiverdIntent = intent
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

        } else if (receivedAction == Intent.ACTION_MAIN) {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

    }

    override fun onItemClick(item: String) {
        Log.d("item", item)
    }
}
