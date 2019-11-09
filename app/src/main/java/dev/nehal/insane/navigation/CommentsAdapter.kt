package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.ContentDTO


class CommentsAdapter(private val list: ArrayList<ContentDTO.Comment>)
    : RecyclerView.Adapter<CommentsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CommentsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val comment: ContentDTO.Comment = list[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = list.size

}
