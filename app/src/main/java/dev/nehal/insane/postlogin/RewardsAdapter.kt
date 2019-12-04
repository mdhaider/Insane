package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.Rewards


class RewardsAdapter(
    private val rewardsList: ArrayList<Rewards>,
    private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return RewardsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: RewardsViewHolder, position: Int) {
        val rewards: Rewards = rewardsList[position]
        holder.bind(rewards, itemClickListener)
    }

    override fun getItemCount(): Int = rewardsList.size
}

