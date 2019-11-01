package dev.nehal.insane.modules.postliked

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.R

class LikedPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var post: TextView
    var time: TextView

 
    init {
        post = view.findViewById(R.id.tvPost)
        time = view.findViewById(R.id.tvTime)


    }
}