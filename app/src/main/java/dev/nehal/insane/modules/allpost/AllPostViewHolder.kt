package dev.nehal.insane.modules.allpost

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.R

class AllPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var img: ImageView
    var post: TextView
    var user: TextView
    var time: TextView
 
    init {
        user=view.findViewById(R.id.tvUser)
        img = view.findViewById(R.id.imgPost)
        post = view.findViewById(R.id.tvPost)
        time = view.findViewById(R.id.tvTime)
    }
}