package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dev.nehal.insane.R
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.shared.TimeAgo


class CommentsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_comment, parent, false)) {
    private var mUserImage: ImageView? = null
    private var mUserNam: TextView? = null
    private var mComment: TextView? = null
    private var mTimeAgo: TextView? = null

    init {
        mUserImage = itemView.findViewById(R.id.imgProf)
        mUserNam = itemView.findViewById(R.id.tvProfName)
        mComment = itemView.findViewById(R.id.tvComment)
        mTimeAgo = itemView.findViewById(R.id.tvAgo)
    }

    fun bind(comment: ContentDTO.Comment) {

        Glide.with(itemView.context)
            .load(comment.userProfImgUrl)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .apply(RequestOptions().circleCrop())
            .into(mUserImage!!)

        mUserNam?.text = comment.userName
        mComment?.text = comment.comment
        mTimeAgo?.text = TimeAgo.getTimeAgo(comment.commentDate!!)
    }
}
