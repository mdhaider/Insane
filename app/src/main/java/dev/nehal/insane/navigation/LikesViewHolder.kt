package dev.nehal.insane.navigation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.model.Users


class LikesViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_likes, parent, false)) {
    private var mUserImage: ImageView? = null
    private var mUserNam: TextView? = null


    init {
        mUserImage = itemView.findViewById(R.id.imgProf)
        mUserNam = itemView.findViewById(R.id.tvProfName)
    }

    fun bind(uid: String) {

        FirebaseFirestore.getInstance()
            .collection("users").document(uid).get().addOnSuccessListener { result ->
                val user = result.toObject(Users::class.java)
                mUserNam?.text = user?.userName

                Glide.with(itemView.context)
                    .load(user?.profImageUri)
                    .error(R.drawable.ic_account)
                    .placeholder(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .apply(RequestOptions().circleCrop())
                    .into(mUserImage!!)
            }

            .addOnFailureListener { exception ->

                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }
    }

}
