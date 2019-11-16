package dev.nehal.insane.navigation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.Users
import dev.nehal.insane.shared.TimeAgo


class AlarmViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_alarm, parent, false)) {
    private var mUserImage: ImageView? = null
    private var mUserNam: TextView? = null
    private var mComment: TextView? = null
    private var mTimeAgo: TextView? = null
    private var mPostImage: ImageView? = null

    init {
        mUserImage = itemView.findViewById(R.id.imgProf)
        mUserNam = itemView.findViewById(R.id.tvProfName)
        mComment = itemView.findViewById(R.id.tvComment)
        mTimeAgo = itemView.findViewById(R.id.tvAgo)
        mPostImage = itemView.findViewById(R.id.imgPostSmall)
    }

    fun bind(alarmDTO: AlarmDTO, itemClickListener:(Int)->Unit) {

        when(alarmDTO.kind){
            0 -> {
                mComment?.text =itemView.context.getString(R.string.message_liked)

            }
            1 -> {
                mComment?.text =itemView.context.getString(R.string.message_alarm,alarmDTO.message)

            }

            2 -> {
                mComment?.text =itemView.context.getString(R.string.message_liked)
            }
        }

        mTimeAgo?.text= TimeAgo.getTimeAgo(alarmDTO.activityDate!!)

        Glide.with(itemView.context)
            .load(alarmDTO.imageUri)
            .error(R.drawable.placeholder_image_new)
            .placeholder(R.drawable.placeholder_image_new)
            .into(mPostImage!!)

        itemView.setOnClickListener { itemClickListener(adapterPosition) }

        setProfile(alarmDTO.uid!!)

    }

    private fun setProfile(uid: String) {
        val dbRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        dbRef.get()
            .addOnSuccessListener { document ->
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    mUserNam?.text = users.userName
                    Glide.with(mUserImage!!.context)
                        .load(users.profImageUri)
                        .error(R.drawable.ic_account)
                        .placeholder(R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(mUserImage!!)
                }

            }.addOnFailureListener { exception ->

                Log.d("ProfileFragment", exception.toString())
            }
    }
}
