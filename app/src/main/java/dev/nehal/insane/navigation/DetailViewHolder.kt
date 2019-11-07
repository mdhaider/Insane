package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import dev.nehal.insane.R
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.shared.TimeAgo


class DetailViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_detail, parent, false)) {
    private var mProfImage: ImageView? = null
    private var mProfName: TextView? = null
    private var mAgo: TextView? = null
    private var mMore: ImageView? = null
    private var mPostImage: ImageView? = null
    private var mFavImage: ImageView? = null
    private var mComImage: ImageView? = null
    private var mLikeCount: TextView? = null
    private var mCaption: TextView? = null
    private var mCommentCount: TextView? = null

    init {
        mProfImage = itemView.findViewById(R.id.imgDetProf)
        mProfName = itemView.findViewById(R.id.tvDetName)
        mAgo = itemView.findViewById(R.id.tvDetAgo)
        mMore = itemView.findViewById(R.id.imgDetMore)
        mPostImage = itemView.findViewById(R.id.imgDetPost)
        mFavImage = itemView.findViewById(R.id.imgDetProf)
        mComImage = itemView.findViewById(R.id.imgDetCom)
        mCaption = itemView.findViewById(R.id.tvDetcaption)
        mLikeCount = itemView.findViewById(R.id.tvDetLikes)
        mCommentCount = itemView.findViewById(R.id.tvDetComments)
    }

    fun bind(contentDTO: ContentDTO, listener: DetailAdapter.ItemClickListener) {

        FirebaseFirestore.getInstance().collection("profileImages").document(contentDTO.uid!!)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result!!["image"]
                    Glide.with(itemView.context)
                        .load(url)
                        .error(R.drawable.ic_account)
                        .placeholder(R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(mProfImage!!)

                }
            }

        mProfName?.text = contentDTO.userName

        mLikeCount?.text = itemView.context.getString(R.string.likes_count, contentDTO.favoriteCount.toString())

        mCommentCount?.text = itemView.context.getString(R.string.likes_count, contentDTO.favoriteCount.toString())

        mAgo?.text = TimeAgo.getTimeAgo(contentDTO.timestamp!!)

        mCaption?.text = contentDTO.explain

        Glide.with(itemView.context)
            .load(contentDTO.imageUrl)
            .error(R.drawable.default_header)
            .placeholder(R.drawable.default_header)
            .into(mPostImage!!)

        mPostImage?.setOnClickListener { listener.doSomething() }

        mLikeCount?.setOnClickListener {  listener.sayHi()}
    }
}
