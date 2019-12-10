package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dev.nehal.insane.R
import dev.nehal.insane.model.AlarmDTO
import dev.nehal.insane.model.ContentDTO
import dev.nehal.insane.model.Users


class PeopleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_all_users, parent, false)) {
    private var mTitleView: TextView? = null
    private var mBlockUser: TextView? = null
    private var mAppUser: TextView? = null
    private var mUploadCount: TextView? = null
    private var mFavCount: TextView? = null
    private var mCommCount: TextView? = null
    private var mUserImage: ImageView? = null
    private var mAdminLayout: View? = null

    init {
        mTitleView = itemView.findViewById(R.id.userName)
        mUploadCount = itemView.findViewById(R.id.uploadCount)
        mFavCount = itemView.findViewById(R.id.favCount)
        mCommCount = itemView.findViewById(R.id.commCount)
        mUserImage = itemView.findViewById(R.id.userImage)
        mBlockUser = itemView.findViewById(R.id.btnBlock)
        mAppUser = itemView.findViewById(R.id.btnApprove)
        mAdminLayout = itemView.findViewById(R.id.adminlayout)

    }

    fun bind(users: Users, contentList: ArrayList<ContentDTO>,commentList: ArrayList<AlarmDTO>,isAdmin:Boolean, itemClickListener: (Int) -> Unit, onItemClickListener: PeopleAdapter.OnItemClickListener) {
        mTitleView?.text = users.userName

        Glide.with(mTitleView!!.context)
            .load(users.profImageUri)
            .error(R.drawable.ic_account)
            .placeholder(R.drawable.ic_account)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions().circleCrop())
            .into(mUserImage!!)

        itemView.setOnClickListener { itemClickListener(adapterPosition) }

        if(isAdmin){
            mAdminLayout?.visibility=View.VISIBLE

            if(users.isApproved){
                mAppUser?.text= "UnApprove"
            } else mAppUser?.text= "Approve"

            if(users.isBlocked){
                mBlockUser?.text= "Unblock"
            } else mBlockUser?.text= "Block"
        }

        var count=0
        var favCount=0

        for (item: ContentDTO in contentList) {
            if(users.userUID==item.uid){
               count=count+1
               favCount=favCount+item.favoriteCount
            }
        }

        var commCount=0
        for (item: AlarmDTO in commentList) {
            if(users.userUID==item.destinationUid && item.kind==1){
                commCount=commCount+1
            }
        }


        mUploadCount?.text=count.toString()
        mFavCount?.text=favCount.toString()
        mCommCount?.text=commCount.toString()

        mAppUser?.setOnClickListener { onItemClickListener.approveUser(users) }
        mBlockUser?.setOnClickListener { onItemClickListener.blockUser(users) }
    }

}
