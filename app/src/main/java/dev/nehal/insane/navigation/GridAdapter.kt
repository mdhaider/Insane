package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.ContentDTO


class GridAdapter(private val list: ArrayList<ContentDTO>, private val itemClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<GridViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GridViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val contentDTO: ContentDTO = list[position]
        holder.bind(contentDTO,itemClickListener)
    }
    override fun getItemCount(): Int = list.size

}
