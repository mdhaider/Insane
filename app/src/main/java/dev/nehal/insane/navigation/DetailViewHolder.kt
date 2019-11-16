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
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users
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

    fun bind(
        contentUid: String,
        contentDTO: ContentDTO,
        listener: DetailAdapter.ItemClickListener
    ) {


        FirebaseFirestore.getInstance()
            .collection("uploadedImages").document(contentUid).collection("comments").get()
            .addOnSuccessListener { result ->

                Log.d("rtg", result.size().toString())
                mCommentCount?.text =
                    itemView.context.getString(R.string.comments_count, result.size().toString())

            }
            .addOnFailureListener { exception ->
                Log.d("DetailFrag", "Error getting documents: ", exception)

            }


        mLikeCount?.text =
            itemView.context.getString(R.string.likes_count, contentDTO.favoriteCount.toString())

        mAgo?.text = TimeAgo.getTimeAgo(contentDTO.imgUploadDate!!)

        mCaption?.text = contentDTO.imgCaption

        Glide.with(itemView.context)
            .load(contentDTO.imgUrl)
            .error(R.drawable.placeholder_image_new)
            .placeholder(R.drawable.placeholder_image_new)
            .into(mPostImage!!)

        mProfImage?.setOnClickListener { listener.goToprofile() }
        mMore?.setOnClickListener { listener.getMore() }
        mPostImage?.setOnClickListener { listener.goToDetailPost() }
        mFavImage?.setOnClickListener { listener.setfav() }
        mComImage?.setOnClickListener {
            listener.goToComments(
                contentDTO.imgUrl!!,
                contentUid,
                contentDTO.uid!!
            )
        }
        mLikeCount?.setOnClickListener { listener.goToLikes(contentDTO.favorites.keys.toList()) }
        mCommentCount?.setOnClickListener {
            listener.goToComments(
                contentDTO.imgUrl!!,
                contentUid,
                contentDTO.uid!!
            )
        }

        setProfile(contentDTO.uid!!)

    }

    private fun setProfile(uid: String) {
        val dbRef = FirebaseFirestore.getInstance().collection("users").document(uid)

        dbRef.get()
            .addOnSuccessListener { document ->
                val users = document.toObject(Users::class.java)
                if (users != null) {
                    mProfName?.text = users.userName
                    Glide.with(mProfImage!!.context)
                        .load(users.profImageUri)
                        .error(R.drawable.ic_account)
                        .placeholder(R.drawable.ic_account)
                        .apply(RequestOptions().circleCrop())
                        .into(mProfImage!!)
                }

            }.addOnFailureListener { exception ->

                Log.d("ProfileFragment", exception.toString())
            }
    }
}
