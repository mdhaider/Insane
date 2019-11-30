package dev.nehal.insane.postlogin

import `in`.myinnos.androidscratchcard.ScratchCard
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.R


class RewardsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_rewards, parent, false)) {
    private var mScratchView: ScratchCard? = null

    init {
        mScratchView = itemView.findViewById(R.id.scratchCard)
    }

    fun bind(itemClickListener: (Int) -> Unit) {
        itemView.setOnClickListener { itemClickListener(adapterPosition) }

    }
}
