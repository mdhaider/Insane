package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.model.Rewards


class AllRewardsAdapter(
    private val rewardsList: ArrayList<Rewards>
) : RecyclerView.Adapter<AllRewardsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRewardsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AllRewardsViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: AllRewardsViewHolder, position: Int) {
        val rewards: Rewards = rewardsList[position]
        holder.bind(rewards)
    }

    override fun getItemCount(): Int = rewardsList.size
}

