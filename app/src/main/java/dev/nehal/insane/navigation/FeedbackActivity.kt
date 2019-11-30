package dev.nehal.insane.navigation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.databinding.ActivityFeedbackBinding
import dev.nehal.insane.model.Feedback
import dev.nehal.insane.model.Users
import dev.nehal.insane.shared.Const
import dev.nehal.insane.shared.ModelPreferences


class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        binding.btnSend.setOnClickListener { sendFeedback() }
        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun sendFeedback() {

        binding.progressBar.visibility= View.VISIBLE

        val user = ModelPreferences(this).getObject(Const.PROF_USER, Users::class.java)!!
        val db = FirebaseFirestore.getInstance()

        try {
            val feedback = Feedback(user.userUID)
            feedback.uid = user.userUID
            feedback.phoneNumber = user.phoneNumber
            feedback.userName = user.userName
            feedback.feedback = binding.feedBack.text.toString()
            feedback.feedbackDate = System.currentTimeMillis()

            db.collection("feedback").add(feedback)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: $documentReference")
                    binding.progressBar.visibility= View.GONE
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility= View.GONE

                }
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            binding.progressBar.visibility= View.GONE
        }
    }
}