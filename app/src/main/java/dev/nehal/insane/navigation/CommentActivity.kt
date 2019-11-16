package dev.nehal.insane.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityCommentBinding
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences
import dev.nehal.insane.util.FcmPush


class CommentActivity : AppCompatActivity() {
    var contentUid: String? = null
    var user: FirebaseUser? = null
    var destinationUid: String? = null
    var imageUri:String?=null
    var fcmPush: FcmPush? = null
    private lateinit var commentsList: ArrayList<ContentDTO.Comment>
    var commentSnapshot: ListenerRegistration? = null
    private lateinit var adapter:CommentsAdapter
    private lateinit var binding: ActivityCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)

        user = FirebaseAuth.getInstance().currentUser
        destinationUid = intent.getStringExtra("destinationUid")
        contentUid = intent.getStringExtra("contentUid")
        imageUri=intent.getStringExtra("imageUri")
        fcmPush = FcmPush()

        binding.postComment.setOnClickListener {
            sendComments()
        }

        binding.imgBack.setOnClickListener{
            finish()
        }

        commentsList = ArrayList()
        binding.rvComment.setHasFixedSize(true)
        adapter= CommentsAdapter(commentsList)
        binding.rvComment.adapter = adapter
        binding.rvComment.layoutManager = LinearLayoutManager(this)

        getComments()

    }

    private fun sendComments() {
        val comment = ContentDTO.Comment()
        val user = ModelPreferences(this).getObject(Const.PROF_USER, Users::class.java)
        comment.uid = user?.userUID
        comment.comment = binding.etPost.text.toString()
        comment.userName = user?.userName
        comment.userProfImgUrl=user?.profImageUri
        comment.commentDate = System.currentTimeMillis()

        FirebaseFirestore.getInstance()
            .collection("uploadedImages")
            .document(contentUid!!)
            .collection("comments")
            .document()
            .set(comment)

        commentAlarm(destinationUid!!, binding.etPost.text.toString())
        binding.etPost.setText("")
    }


    override fun onStop() {
        super.onStop()
        commentSnapshot?.remove()
    }

    private fun getComments(){
        commentSnapshot = FirebaseFirestore
            .getInstance()
            .collection("uploadedImages")
            .document(contentUid!!)
            .collection("comments").orderBy("commentDate", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                commentsList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    commentsList.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                }
                adapter.notifyDataSetChanged()
                binding.tvCommCount.text = getString(R.string.comments_count, commentsList.size.toString())

            }
    }


    private fun commentAlarm(destinationUid: String, message: String) {
        val alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = user?.phoneNumber
        alarmDTO.username = user?.displayName
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 1
        alarmDTO.message = message
        alarmDTO.contentId = contentUid
        alarmDTO.imageUri= imageUri
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message =
            user?.displayName + getString(R.string.alarm_comment) + " " + message
        fcmPush?.sendMessage(destinationUid, "This is a notification message.", message)
    }
}