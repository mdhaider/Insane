package dev.nehal.insane.navigation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.ContentDTO


class ProfGridAdapter(private val list: ArrayList<ContentDTO>, private val itemClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<ProfGridViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfGridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProfGridViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ProfGridViewHolder, position: Int) {
        val contentDTO: ContentDTO = list[position]
        holder.bind(contentDTO,itemClickListener)
    }
    override fun getItemCount(): Int = list.size

}
