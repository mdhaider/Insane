package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class LikesAdapter(private val list: ArrayList<String>)
    : RecyclerView.Adapter<LikesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LikesViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val like: String= list[position]
        holder.bind(like)
    }

    override fun getItemCount(): Int = list.size

}
