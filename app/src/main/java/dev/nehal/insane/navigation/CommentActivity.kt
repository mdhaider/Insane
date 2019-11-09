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
import dev.nehal.insane.util.FcmPush


class CommentActivity : AppCompatActivity() {
    var contentUid: String? = null
    var user: FirebaseUser? = null
    var destinationUid: String? = null
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
        comment.userId = FirebaseAuth.getInstance().currentUser!!.phoneNumber
        comment.comment = binding.etPost.text.toString()
        comment.username = FirebaseAuth.getInstance().currentUser!!.displayName
        comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
        comment.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance()
            .collection("images")
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
            .collection("images")
            .document(contentUid!!)
            .collection("comments").orderBy("timestamp", Query.Direction.DESCENDING)
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
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message =
            user?.displayName + getString(R.string.alarm_comment) + " " + message
        fcmPush?.sendMessage(destinationUid, "This is a notification message.", message)
    }
}