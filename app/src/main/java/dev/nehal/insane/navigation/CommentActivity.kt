package dev.nehal.insane.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
    var destinationUid: String? = null
    var imageUri:String?=null
    var fcmPush: FcmPush? = null
    private lateinit var commentsList: ArrayList<ContentDTO.Comment>
    var commentSnapshot: ListenerRegistration? = null
    private lateinit var adapter:CommentsAdapter
    private lateinit var binding: ActivityCommentBinding
    private lateinit var  user:Users
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment)

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
        user = ModelPreferences(this).getObject(Const.PROF_USER, Users::class.java)!!
        comment.uid = user.userUID
        comment.comment = binding.etPost.text.toString()
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
        alarmDTO.contentUid = contentUid
        alarmDTO.destinationUid = destinationUid
        alarmDTO.uid = user.userUID
        alarmDTO.imageUri= imageUri
        alarmDTO.kind = 1
        alarmDTO.message = message
        alarmDTO.activityDate = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("userActivities").document().set(alarmDTO)

        val msg =
            user.userName + getString(R.string.alarm_comment) + " " + message
        fcmPush?.sendMessage(destinationUid, "This is a notification message.", msg)
    }
}