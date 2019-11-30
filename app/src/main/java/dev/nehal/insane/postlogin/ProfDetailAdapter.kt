package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.ContentDTO


class ProfDetailAdapter(
    private val list: ArrayList<ContentDTO>,
    private val contentUidList: ArrayList<String>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<ProfDetailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProfDetailViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProfDetailViewHolder, position: Int) {
        val contentDTO: ContentDTO = list[position]
        val contentUid: String = contentUidList[position]
        holder.bind(contentUid,contentDTO, listener)
    }

    override fun getItemCount(): Int = list.size

    interface ItemClickListener {
        fun goToProfile(userUid: String)
        fun getMore()
        fun goToDetailPost(contentId: String,contentDTO: ContentDTO)
        fun setfav(contentUid: String, destUid:String)
        fun goToLikes(uidList:List<String>)
        fun goToComments(imageUri:String, contentUid: String, userUid: String)
    }
}
