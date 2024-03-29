package dev.nehal.insane.postlogin

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
import dev.nehal.insane.model.Rewards
import dev.nehal.insane.model.Users
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AllRewardsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.all_item_rewards, parent, false)) {
    private var mUserImage: ImageView? = null
    private var mUserNam: TextView? = null
    private var mCoin: TextView? = null
    private var mCoindate: TextView? = null
    init {
        mUserImage = itemView.findViewById(R.id.imgRew)
        mUserNam = itemView.findViewById(R.id.rewName)
        mCoin = itemView.findViewById(R.id.rewCoin)
        mCoindate = itemView.findViewById(R.id.rewDate)
    }

    fun bind(rewards: Rewards) {
        FirebaseFirestore.getInstance()
            .collection("users").document(rewards.uid).get().addOnSuccessListener { result ->
                val user = result.toObject(Users::class.java)
                mUserNam?.text = user?.userName

                Glide.with(itemView.context)
                    .load(user?.profImageUri)
                    .error(R.drawable.ic_account)
                    .placeholder(R.drawable.ic_account)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions().circleCrop())
                    .into(mUserImage!!)
            }

            .addOnFailureListener { exception ->
                Log.d("PeopleFrag", "Error getting documents: ", exception)
            }

        if(rewards.isRevealed){
            mCoin?.text = rewards.coinValue.toString()
        } else{
            mCoin?.text = "--"
        }

        mCoindate?.text= datetoString(rewards.creationDate)

    }

    fun datetoString(dates: Long): String? {
        val date1 = Date(dates)
        val sdf = SimpleDateFormat("MMM dd, yyyy",
            Locale.ENGLISH)
        var date: String?=null
        try {
            date = sdf.format(date1)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

}
