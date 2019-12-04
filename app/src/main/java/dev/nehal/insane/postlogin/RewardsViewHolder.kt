package dev.nehal.insane.postlogin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.R
import dev.nehal.insane.model.Rewards
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RewardsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_rewards, parent, false)) {
    private var mCoinText: TextView? = null
    private var mCoinDate: TextView? = null
    private var root: View? = null
    private var rootChild: View? = null

    init {
        rootChild = itemView.findViewById(R.id.rootChild)
        root = itemView.findViewById(R.id.rootLayout1)
        mCoinText = itemView.findViewById(R.id.coinText)
        mCoinDate = itemView.findViewById(R.id.dateCoin)
    }

    fun bind(rewards: Rewards, itemClickListener: (Int) -> Unit) {

        if (rewards.isRevealed) {
            root?.setBackgroundColor(root!!.resources.getColor(R.color.colorWhite))
            rootChild?.visibility = View.VISIBLE
            mCoinDate?.text = datetoString(rewards.creationDate)
            mCoinText?.text = rewards.coinValue.toString()
        }
        itemView.setOnClickListener { itemClickListener(adapterPosition) }

    }

    fun datetoString(dates: Long): String? {
        val date1 = Date(dates)
        val sdf = SimpleDateFormat("MMM dd, yyyy",
            Locale.ENGLISH)
        var date: String?=null
        try {
            date = sdf.format(date1)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }
}
