package dev.nehal.insane.modules.userlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.nehal.insane.R

class UserListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var name: TextView
    var phone:TextView
    var time: TextView
    var imgProf:ImageView
    var llAdmin:View
    var tvStatus: TextView
 
    init {
        name=view.findViewById(R.id.tvName)
        phone = view.findViewById(R.id.tvPhone)
        time = view.findViewById(R.id.tvDate)
        imgProf=view.findViewById(R.id.imgProf)
        llAdmin = view.findViewById(R.id.llAdmin)
        tvStatus = view.findViewById(R.id.tvStatus)

    }
}