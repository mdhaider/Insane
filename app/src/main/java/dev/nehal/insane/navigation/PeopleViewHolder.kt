package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dev.nehal.insane.R
import dev.nehal.insane.modules.login.User


class PeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_all_users, parent, false)) {
    private var mTitleView: TextView? = null
    private var mUserImage: ImageView? = null

    init {
        mTitleView = itemView.findViewById(R.id.userName)
        mUserImage = itemView.findViewById(R.id.userImage)
    }

    fun bind(user: User) {
        mTitleView?.text = user.mName

        Glide.with(mTitleView!!.context)
            .load(user.imageuri)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .apply(RequestOptions().circleCrop())
            .into(mUserImage!!)
    }

}