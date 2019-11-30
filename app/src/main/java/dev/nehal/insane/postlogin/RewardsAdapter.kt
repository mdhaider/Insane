package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class RewardsAdapter(private val itemClickListener: (Int) -> Unit )
    : RecyclerView.Adapter<RewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RewardsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RewardsViewHolder, position: Int) {
        holder.bind(itemClickListener)
    }

    override fun getItemCount(): Int = 10

}
