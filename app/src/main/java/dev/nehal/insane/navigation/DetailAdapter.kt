package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.ContentDTO


class DetailAdapter(
    private val list: ArrayList<ContentDTO>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<DetailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DetailViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val contentDTO: ContentDTO = list[position]
        holder.bind(contentDTO, listener)
    }

    override fun getItemCount(): Int = list.size

    interface ItemClickListener {
        fun goToprofile()
        fun getMore()
        fun goToDetailPost()
        fun setfav()
        fun goToComment()
        fun goToLikes()
        fun goToComments()
    }

}
