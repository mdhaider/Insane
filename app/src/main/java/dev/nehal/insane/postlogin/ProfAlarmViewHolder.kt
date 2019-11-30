package dev.nehal.insane.postlogin

import android.util.Log
import android.view.LayoutInflater
import android.view.View
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


class ProfAlarmViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_alarm, parent, false)) {
    private var mUserImage: ImageView? = null
    private var mUserNam: TextView? = null
    private var mComment: TextView? = null
    private var mTimeAgo: TextView? = null
    private var mPostImage: ImageView? = null
    private var mImageType: ImageView?=null
    private var mImageBack: View?=null

    init {
        mUserImage = itemView.findViewById(R.id.imgProf)
        mUserNam = itemView.findViewById(R.id.tvProfName)
        mComment = itemView.findViewById(R.id.tvComment)
        mTimeAgo = itemView.findViewById(R.id.tvAgo)
        mPostImage = itemView.findViewById(R.id.imgPostSmall)
        mImageType = itemView.findViewById(R.id.imgType)
        mImageBack = itemView.findViewById(R.id.imgBack)
    }

    fun bind(alarmDTO: AlarmDTO, itemClickListener:(Int)->Unit) {

        when(alarmDTO.kind){
            0 -> {
                mComment?.text =itemView.context.getString(R.string.message_liked)
                mImageType?.setImageResource(R.drawable.ic_favorite_red)
                mImageBack?.setBackgroundResource(R.drawable.circle_2)

            }
            1 -> {
                mComment?.text =itemView.context.getString(R.string.message_ala)
                mImageType?.setImageResource(R.drawable.ic_mode_comment_black_24dp)
                mImageBack?.setBackgroundResource(R.drawable.circle_1)

            }

            2 -> {
                mComment?.text =itemView.context.getString(R.string.message_liked)
                mImageType?.setImageResource(R.drawable.ic_favorite_red)
                mImageBack?.setBackgroundResource(R.drawable.circle_2)

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
